package com.ridehub.route.repository;

import com.ridehub.route.domain.Trip;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;

import java.time.Instant;

import java.time.Instant;

/**
 * Spring Data JPA repository for Trip entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {

    /**
     * Count trips by route, slot, departure time range, and not deleted.
     * Optimized query for fast existence checking.
     */
    long countByRouteIdAndSlotIdAndDepartureTimeBetweenAndIsDeletedFalse(
            Long routeId, 
            Long slotId, 
            Instant departureTimeStart, 
            Instant departureTimeEnd
    );
}
