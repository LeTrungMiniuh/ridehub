package com.ridehub.route.service;

import com.ridehub.route.domain.enumeration.OccasionType;
import com.ridehub.route.domain.enumeration.SeatType;
import com.ridehub.route.domain.enumeration.VehicleType;
import com.ridehub.route.service.criteria.PricingTemplateCriteria;
import com.ridehub.route.service.dto.PricingTemplateDTO;
import com.ridehub.route.service.dto.TripDTO;
import com.ridehub.route.service.dto.TripWithPricingDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for handling trip pricing calculations and conversions.
 */
@Service
@Transactional(readOnly = true)
public class TripPricingService {

    private final PricingTemplateQueryService pricingTemplateQueryService;
    private final FloorQueryService floorQueryService;
    private final SeatQueryService seatQueryService;

    public TripPricingService(PricingTemplateQueryService pricingTemplateQueryService,
            FloorQueryService floorQueryService,
            SeatQueryService seatQueryService) {
        this.pricingTemplateQueryService = pricingTemplateQueryService;
        this.floorQueryService = floorQueryService;
        this.seatQueryService = seatQueryService;
    }

    /**
     * Convert TripDTO to TripWithPricingDTO with pricing templates.
     */
    public TripWithPricingDTO convertToTripWithPricingDTO(TripDTO tripDTO) {
        TripWithPricingDTO tripWithPricing = new TripWithPricingDTO();

        // Copy properties
        tripWithPricing.setId(tripDTO.getId());
        tripWithPricing.setTripCode(tripDTO.getTripCode());
        tripWithPricing.setDepartureTime(tripDTO.getDepartureTime());
        tripWithPricing.setArrivalTime(tripDTO.getArrivalTime());
        tripWithPricing.setOccasionFactor(tripDTO.getOccasionFactor());
        tripWithPricing.setCreatedAt(tripDTO.getCreatedAt());
        tripWithPricing.setUpdatedAt(tripDTO.getUpdatedAt());
        tripWithPricing.setIsDeleted(tripDTO.getIsDeleted());
        tripWithPricing.setDeletedAt(tripDTO.getDeletedAt());
        tripWithPricing.setDeletedBy(tripDTO.getDeletedBy());
        tripWithPricing.setRoute(tripDTO.getRoute());
        tripWithPricing.setVehicle(tripDTO.getVehicle());
        tripWithPricing.setSlot(tripDTO.getSlot());
        tripWithPricing.setDriver(tripDTO.getDriver());
        tripWithPricing.setAttendant(tripDTO.getAttendant());

        // Calculate and set pricing templates
        tripWithPricing.setPricingTemplates(calculatePricingTemplates(tripDTO));

        return tripWithPricing;
    }

    /**
     * Calculate pricing templates for a trip.
     */
    private List<PricingTemplateDTO> calculatePricingTemplates(TripDTO tripDTO) {
        List<PricingTemplateDTO> existingTemplates = fetchExistingPricingTemplates(tripDTO);
        VehicleType vehicleType = tripDTO.getVehicle() != null ? tripDTO.getVehicle().getType() : null;

        if (vehicleType == null) {
            return existingTemplates.stream()
                    .map(template -> calculateTemplateValues(template, tripDTO))
                    .collect(Collectors.toList());
        }

        List<PricingTemplateDTO> result = existingTemplates.stream()
                .map(template -> calculateTemplateValues(template, tripDTO))
                .collect(Collectors.toList());

        // Optimized single-query approach to get seat types and floor factors
        Long seatMapId = tripDTO.getVehicle().getSeatMap().getId();
        Map<SeatType, BigDecimal> seatTypeFactors = seatQueryService.findSeatTypesWithFactorsBySeatMapId(seatMapId);
        Map<Integer, BigDecimal> floorFactors = floorQueryService.findFloorFactorsBySeatMapId(seatMapId);

        Set<SeatType> vehicleSeatTypes = seatTypeFactors.keySet();
        // Set<OccasionType> occasionTypes = EnumSet.allOf(OccasionType.class);

        // Loop through seatTypeFactors and floorFactors
        for (SeatType seatType : vehicleSeatTypes) {
            BigDecimal seatFactor = seatTypeFactors.get(seatType);

            for (Integer floorNumber : floorFactors.keySet()) {
                BigDecimal floorFactor = floorFactors.get(floorNumber);

                boolean exists = existingTemplates.stream()
                        .anyMatch(t -> t.getVehicleType() == vehicleType &&
                                t.getSeatType() == seatType);

                if (!exists) {
                    result.add(createNewPricingTemplate(vehicleType, seatType, tripDTO,
                            seatFactor, floorFactor,
                            tripDTO.getVehicle().getTypeFactor(), tripDTO.getOccasionFactor()));
                }
            }
        }

        return result;
    }

    /**
     * Create a new pricing template using factors from maps and trip data.
     */
    private PricingTemplateDTO createNewPricingTemplate(VehicleType vehicleType, SeatType seatType,TripDTO tripDTO, BigDecimal seatFactor,
            BigDecimal floorFactor, BigDecimal vehicleFactor, BigDecimal occasionFactor) {

        PricingTemplateDTO template = new PricingTemplateDTO();

        // Set basic properties
        template.setVehicleType(vehicleType);
        template.setSeatType(seatType);
        // template.setOccasionType(occasionType);
        template.setRoute(tripDTO.getRoute());

        // Set factors from parameters
        template.setVehicleFactor(vehicleFactor);
        template.setSeatFactor(seatFactor);
        template.setFloorFactor(floorFactor);
        template.setOccasionFactor(occasionFactor);

        // Calculate base fare and final price
        template.setBaseFare(tripDTO.getRoute().getBaseFare());
        template.setFinalPrice(calculateFinalPrice(template));

        // Set timestamps
        template.setCreatedAt(Instant.now());
        template.setUpdatedAt(Instant.now());
        template.setIsDeleted(false);

        return template;
    }

    /**
     * Fetch existing pricing templates for the trip's route.
     */
    private List<PricingTemplateDTO> fetchExistingPricingTemplates(TripDTO tripDTO) {
        if (tripDTO.getRoute() == null || tripDTO.getRoute().getId() == null) {
            return new ArrayList<>();
        }

        PricingTemplateCriteria criteria = new PricingTemplateCriteria();
        criteria.routeId().setEquals(tripDTO.getRoute().getId());
        criteria.vehicleType().setEquals(tripDTO.getVehicle().getType());
        criteria.occasionFactor().setEquals(tripDTO.getOccasionFactor());
        criteria.isDeleted().setEquals(false);

        return pricingTemplateQueryService.findByCriteria(criteria, Pageable.unpaged()).getContent();
    }

    /**
     * Calculate missing values for a pricing template.
     */
    private PricingTemplateDTO calculateTemplateValues(PricingTemplateDTO template, TripDTO tripDTO) {
        PricingTemplateDTO calculated = new PricingTemplateDTO();

        // Copy properties
        calculated.setId(template.getId());
        calculated.setVehicleType(template.getVehicleType());
        calculated.setSeatType(template.getSeatType());
        calculated.setOccasionType(template.getOccasionType());
        calculated.setBaseFare(template.getBaseFare());
        calculated.setVehicleFactor(template.getVehicleFactor());
        calculated.setFloorFactor(template.getFloorFactor());
        calculated.setSeatFactor(template.getSeatFactor());
        calculated.setOccasionFactor(template.getOccasionFactor());
        calculated.setFinalPrice(template.getFinalPrice());
        calculated.setValidFrom(template.getValidFrom());
        calculated.setValidTo(template.getValidTo());
        calculated.setCreatedAt(template.getCreatedAt());
        calculated.setUpdatedAt(template.getUpdatedAt());
        calculated.setIsDeleted(template.getIsDeleted());
        calculated.setDeletedAt(template.getDeletedAt());
        calculated.setDeletedBy(template.getDeletedBy());
        calculated.setRoute(template.getRoute());
        return calculated;
    }

    /**
     * Calculate final price based on all factors.
     */
    private BigDecimal calculateFinalPrice(PricingTemplateDTO template) {
        BigDecimal baseFare = Optional.ofNullable(template.getBaseFare()).orElse(BigDecimal.ZERO);
        BigDecimal vehicleFactor = Optional.ofNullable(template.getVehicleFactor()).orElse(BigDecimal.ONE);
        BigDecimal seatFactor = Optional.ofNullable(template.getSeatFactor()).orElse(BigDecimal.ONE);
        BigDecimal floorFactor = Optional.ofNullable(template.getFloorFactor()).orElse(BigDecimal.ONE);
        BigDecimal occasionFactor = Optional.ofNullable(template.getOccasionFactor()).orElse(BigDecimal.ONE);

        return baseFare.multiply(vehicleFactor)
                .multiply(seatFactor)
                .multiply(floorFactor)
                .multiply(occasionFactor)
                .setScale(2, RoundingMode.HALF_UP);
    }

}