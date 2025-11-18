package com.ridehub.booking.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.booking.service.dto.AppliedPromotionDTO;
import com.ridehub.booking.service.dto.PricingSnapshotDTO;
import com.ridehub.booking.service.vm.PricingResultVM;
import com.ridehub.mspromotion.client.api.PromotionResourceMspromotionApi;
import com.ridehub.mspromotion.client.model.BuyNGetMFreeDTO;
import com.ridehub.mspromotion.client.model.ConditionRouteItemDTO;
import com.ridehub.mspromotion.client.model.PercentOffTotalDTO;
import com.ridehub.mspromotion.client.model.PromotionDetailDTO;
import com.ridehub.msroute.client.api.PricingTemplateResourceMsrouteApi;
import com.ridehub.msroute.client.api.TripResourceMsrouteApi;
import com.ridehub.msroute.client.model.SeatDTO;
import com.ridehub.msroute.client.model.SeatLockDTO;
import com.ridehub.msroute.client.model.TripDetailVM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Computes final booking price using:
 * - TripDetailVM from ms-route (baseFare, vehicle, floors, seats, locks)
 * - PromotionDetailDTO from ms-promotion (policies + conditions)
 *
 * NOTE: All Redis caching is disabled/commented so this runs without Redis.
 */
@Service
public class PricingService {
    private static final Logger LOG = LoggerFactory.getLogger(PricingService.class);

    private final PromotionResourceMspromotionApi promotionResourceMspromotionApi;
    private final PricingTemplateResourceMsrouteApi pricingTemplateResourceMsrouteApi;
    private final TripResourceMsrouteApi tripResourceMsrouteApi;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public PricingService(
            PromotionResourceMspromotionApi promotionResourceMspromotionApi,
            TripResourceMsrouteApi tripResourceMsrouteApi, StringRedisTemplate redis,
            ObjectMapper objectMapper, PricingTemplateResourceMsrouteApi pricingTemplateResourceMsrouteApi) {
        this.promotionResourceMspromotionApi = promotionResourceMspromotionApi;
        this.pricingTemplateResourceMsrouteApi = pricingTemplateResourceMsrouteApi;
        this.tripResourceMsrouteApi = tripResourceMsrouteApi;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    /**
     * Main entry point used by BookingService.
     */
    public PricingResultVM computePrice(Long tripId, List<String> seatNos, String promoCode) {
        if (seatNos == null || seatNos.isEmpty())
            throw new IllegalArgumentException("Seat list cannot be empty");

        // === 1️⃣ Trip lookup (no cache yet) ===
        TripDetailVM tripVM = tripResourceMsrouteApi.getTripDetail(tripId);

        BigDecimal baseFare = nn(tripVM.getTripDTO().getRoute().getBaseFare(), BigDecimal.ZERO);
        BigDecimal vehicleFactor = nn(tripVM.getDetailVM().getVehicle().getTypeFactor(), BigDecimal.ONE);
        BigDecimal occasionFactor = nn(tripVM.getTripDTO().getOccasionFactor(), BigDecimal.ONE);
        Long routeId = tripVM.getTripDTO().getRoute().getId();
        LocalDate travelDate = toLocalDate(tripVM.getTripDTO().getDepartureTime());

        // === 3️⃣ Compute total base price ===
        List<BigDecimal> perSeatPrices = computeSeatPrices(tripVM, seatNos);
        BigDecimal total = perSeatPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // === 4️⃣ Try promo cache (Redis) ===
        AppliedPromotionDTO applied = null;
        if (promoCode != null && !promoCode.isBlank()) {
            String promoKey = "promo:" + promoCode;
            PromotionDetailDTO detail = null;

            try {
                String cached = redis.opsForValue().get(promoKey);
                if (cached != null) {
                    detail = objectMapper.readValue(cached, PromotionDetailDTO.class);
                    LOG.debug("Promo {} found in Redis cache", promoCode);
                } else {
                    detail = promotionResourceMspromotionApi.getPromotionDetailByCode(promoCode);
                    if (detail != null && detail.getId() != null) {
                        String json = objectMapper.writeValueAsString(detail);
                        redis.opsForValue().set(promoKey, json, Duration.ofMinutes(5));
                        LOG.debug("Promo {} cached in Redis", promoCode);
                    }
                }
            } catch (Exception e) {
                LOG.warn("Promo {} cache failed: {}", promoCode, e.toString());
            }

            if (detail != null && detail.getId() != null) {
                applied = evaluatePromotion(detail, routeId, travelDate, seatNos.size(), perSeatPrices);
                if (applied != null && applied.getDiscountAmount() != null) {
                    total = total.subtract(applied.getDiscountAmount()).max(BigDecimal.ZERO);
                }
            }
        }

        // === 5️⃣ Snapshot result ===
        PricingSnapshotDTO snap = new PricingSnapshotDTO();
        snap.setBaseFare(baseFare);
        snap.setVehicleFactor(vehicleFactor);
        snap.setScheduleOccasionFactor(occasionFactor);
        snap.setFloorFactor(BigDecimal.ONE);
        snap.setSeatFactor(BigDecimal.ONE);
        snap.setFinalPrice(total);

        PricingResultVM out = new PricingResultVM();
        out.setPricingSnapshot(snap);
        out.setAppliedPromotion(applied);
        out.setFinalPrice(total);
        out.setPromoApplied(applied != null);
        return out;
    }

    // =========================
    // Promotion evaluation
    // =========================

    private AppliedPromotionDTO evaluatePromotion(
            PromotionDetailDTO promo,
            Long routeId,
            LocalDate travelDate,
            int seatCount,
            List<BigDecimal> perSeatPrices) {
        if (promo == null)
            return null;

        LocalDate today = (travelDate != null) ? travelDate : LocalDate.now();

        // 1) Date window
        if (promo.getStartDate() != null && today.isBefore(promo.getStartDate()))
            return null;
        if (promo.getEndDate() != null && today.isAfter(promo.getEndDate()))
            return null;

        // 2) Usage limit
        if (promo.getUsageLimit() != null && promo.getUsedCount() != null
                && promo.getUsedCount() >= promo.getUsageLimit())
            return null;

        // 3) Route conditions
        if (promo.getConditionsRS() != null && !promo.getConditionsRS().isEmpty()) {
            boolean routeOk = promo.getConditionsRS().stream()
                    .flatMap(cr -> (cr.getItems() == null ? Stream.<ConditionRouteItemDTO>empty()
                            : cr.getItems().stream()))
                    .anyMatch(it -> Objects.equals(it.getRouteId(), routeId));
            if (!routeOk)
                return null;
        }

        // 4) Date conditions (specific dates or weekdays)
        if (promo.getConditionsDS() != null && !promo.getConditionsDS().isEmpty()) {
            boolean dateOk = promo.getConditionsDS().stream().anyMatch(cd -> {
                boolean specOk = cd.getItems() != null && cd.getItems().stream()
                        .anyMatch(it -> it.getSpecificDate() != null && it.getSpecificDate().equals(today));
                boolean weekdayOk = cd.getItems() != null && cd.getItems().stream()
                        .anyMatch(it -> it.getWeekday() != null && it.getWeekday() == today.getDayOfWeek().getValue());
                return specOk || weekdayOk;
            });
            if (!dateOk)
                return null;
        }

        // 5) Pick best applicable policy
        BigDecimal total = perSeatPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5a) PercentOffTotal — choose highest percent (respect maxOff)
        AppliedPromotionDTO bestPercent = null;
        if (promo.getPercentOffs() != null && !promo.getPercentOffs().isEmpty()) {
            PercentOffTotalDTO best = promo.getPercentOffs().stream()
                    .filter(p -> p.getPercent() != null && p.getPercent() > 0)
                    .max(Comparator.comparingInt(PercentOffTotalDTO::getPercent))
                    .orElse(null);
            if (best != null) {
                BigDecimal off = percent(total, best.getPercent());
                if (best.getMaxOff() != null)
                    off = off.min(best.getMaxOff());

                bestPercent = new AppliedPromotionDTO();
                bestPercent.setPromotionCode(promo.getCode());
                bestPercent.setPolicyType("PERCENT_OFF");
                bestPercent.setPercent(best.getPercent());
                bestPercent.setMaxOff(best.getMaxOff());
                bestPercent.setDiscountAmount(off);
            }
        }

        // 5b) BuyNGetMFree — make M cheapest seats free if seatCount >= N+M
        AppliedPromotionDTO bestBnm = null;
        if (promo.getBuyNGetMS() != null && !promo.getBuyNGetMS().isEmpty()) {
            BuyNGetMFreeDTO best = promo.getBuyNGetMS().stream()
                    .filter(b -> b.getBuyN() != null && b.getGetM() != null)
                    .max(Comparator.comparingInt(BuyNGetMFreeDTO::getGetM))
                    .orElse(null);
            if (best != null && seatCount >= best.getBuyN() + best.getGetM()) {
                List<BigDecimal> sorted = perSeatPrices.stream().sorted().collect(Collectors.toList());
                BigDecimal off = sorted.stream().limit(best.getGetM()).reduce(BigDecimal.ZERO, BigDecimal::add);

                bestBnm = new AppliedPromotionDTO();
                bestBnm.setPromotionCode(promo.getCode());
                bestBnm.setPolicyType("BUY_N_GET_M_FREE");
                bestBnm.setDiscountAmount(off);
            }
        }

        // Choose better discount if both available
        if (bestPercent != null && bestBnm != null) {
            return (bestPercent.getDiscountAmount().compareTo(bestBnm.getDiscountAmount()) >= 0) ? bestPercent
                    : bestBnm;
        }
        if (bestPercent != null)
            return bestPercent;
        if (bestBnm != null)
            return bestBnm;

        return null; // no applicable policy
    }

    // =========================
    // Small utilities
    // =========================
    // ---- helper: compute per-seat prices ----
    private List<BigDecimal> computeSeatPrices(
            TripDetailVM tripVM, List<String> seatNos) {

        Map<String, SeatDTO> seatByNo = new HashMap<>();
        tripVM.getDetailVM().getSeatsByFloorId().forEach((floorIdStr, seats) -> {
            seats.forEach(s -> seatByNo.put(s.getSeatNo(), s));
        });

        List<BigDecimal> perSeat = new ArrayList<>();
        for (String seatNo : seatNos) {
            SeatDTO seat = seatByNo.get(seatNo);
            if (seat == null)
                throw new IllegalArgumentException("Unknown seat number: " + seatNo);
            perSeat.add(pricingTemplateResourceMsrouteApi.getPricingTemplateByTripAndSeat(tripVM.getTripDTO().getId(),seat.getId()).getFinalPrice());
        }
        return perSeat;
    }

    private static boolean eqAnyIgnoreCase(Enum<?> v, String... options) {
        if (v == null)
            return false;
        String name = v.name();
        for (String o : options) {
            if (name.equalsIgnoreCase(o))
                return true;
        }
        return false;
    }

    private static BigDecimal nn(BigDecimal v, BigDecimal def) {
        return v == null ? def : v;
    }

    private static LocalDate toLocalDate(OffsetDateTime odt) {
        if (odt == null)
            return LocalDate.now();
        return odt.toLocalDate();
    }

    private BigDecimal percent(BigDecimal base, Integer percent) {
        if (base == null || percent == null)
            return BigDecimal.ZERO;
        return base.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100));
    }

    // =========================
    // Redis helpers (disabled)
    // =========================
    // private final StringRedisTemplate redis;
    // private static final ObjectMapper M = new ObjectMapper();
    // private <T> T getCached(String key, Class<T> type) { ... }
    // private void setCached(String key, Object value, Duration ttl) { ... }
}
