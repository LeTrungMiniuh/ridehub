package com.ridehub.route.web.rest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ridehub.route.service.LocationSearchService;
import com.ridehub.route.service.TripQueryService;
import com.ridehub.route.service.criteria.TripCriteria;
import com.ridehub.route.service.dto.TripDTO;
import com.ridehub.route.service.dto.request.ChatbotTripSearchRequest;

import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.StringFilter;

@RestController
@RequestMapping("/api/chatbot/trips")
public class ChatbotTripSearchResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChatbotTripSearchResource.class);
    private final LocationSearchService locationSearchService;
    private final TripQueryService tripQueryService; // your existing JHipster query service
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public ChatbotTripSearchResource(LocationSearchService locationSearchService, TripQueryService tripQueryService) {
        this.locationSearchService = locationSearchService;
        this.tripQueryService = tripQueryService;
    }

    @PostMapping("/search")
    public ResponseEntity<Page<TripDTO>> searchTrips(
            @RequestBody ChatbotTripSearchRequest req,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        TripCriteria criteria = new TripCriteria();

        // ORIGIN
        String originCode = locationSearchService.resolveSingleCode(req.getOrigin()).orElse(null);
        if (originCode == null) {
            return ResponseEntity.ok(Page.empty(pageable));
        }
        StringFilter originFilter = new StringFilter();
        originFilter.setEquals(originCode);
        criteria.setOriginProvinceCode(originFilter);

        // DESTINATION
        String destCode = locationSearchService.resolveSingleCode(req.getDestination()).orElse(null);
        if (destCode == null) {
            return ResponseEntity.ok(Page.empty(pageable));
        }
        StringFilter destFilter = new StringFilter();
        destFilter.setEquals(destCode);
        criteria.setDestinationProvinceCode(destFilter);

        // === TIME FILTERS ===
        LocalDate date = req.getDate(); // may be null

        if (date != null) {
            // ----- DEPARTURE TIME -----
            InstantFilter depFilter = new InstantFilter();

            // If null -> full day
            LocalTime depFromTime = req.getDepartureTimeFrom() != null ? req.getDepartureTimeFrom() : LocalTime.MIN;
            LocalTime depToTime = req.getDepartureTimeTo() != null ? req.getDepartureTimeTo() : LocalTime.MAX;

            Instant depFrom = toInstant(date, depFromTime, VN_ZONE);
            // Use next day start as exclusive upper bound if no specific "to" is set
            Instant depTo = req.getDepartureTimeTo() != null
                    ? toInstant(date, depToTime, VN_ZONE)
                    : date.plusDays(1).atStartOfDay(VN_ZONE).toInstant();

            depFilter.setGreaterThanOrEqual(depFrom);
            depFilter.setLessThan(depTo); // [from, to)

            criteria.setDepartureTime(depFilter);

            // ----- ARRIVAL TIME (optional) -----
            if (req.getArrivalTimeFrom() != null || req.getArrivalTimeTo() != null) {
                InstantFilter arrFilter = new InstantFilter();

                LocalTime arrFromTime = req.getArrivalTimeFrom() != null ? req.getArrivalTimeFrom() : LocalTime.MIN;
                LocalTime arrToTime = req.getArrivalTimeTo() != null ? req.getArrivalTimeTo() : LocalTime.MAX;

                Instant arrFrom = toInstant(date, arrFromTime, VN_ZONE);
                Instant arrTo = req.getArrivalTimeTo() != null
                        ? toInstant(date, arrToTime, VN_ZONE)
                        : date.plusDays(1).atStartOfDay(VN_ZONE).toInstant();

                arrFilter.setGreaterThanOrEqual(arrFrom);
                arrFilter.setLessThan(arrTo);

                criteria.setArrivalTime(arrFilter);
            }
        } else {
            // date == null → bỏ filter theo giờ
            LOG.debug("No date provided in ChatbotTripSearchRequest, skipping time filters");
        }

        Page<TripDTO> page = tripQueryService.findByCriteria(criteria, pageable);
        return ResponseEntity.ok(page);
    }

    private Instant toInstant(LocalDate date, LocalTime time, ZoneId zoneId) {
        if (date == null || time == null) {
            return null; // caller must handle null
        }
        return ZonedDateTime.of(date, time, zoneId).toInstant();
    }

}
