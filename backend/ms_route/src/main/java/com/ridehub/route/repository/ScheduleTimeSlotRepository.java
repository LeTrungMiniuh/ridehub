package com.ridehub.route.repository;

import com.ridehub.route.domain.ScheduleTimeSlot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ScheduleTimeSlot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleTimeSlotRepository extends JpaRepository<ScheduleTimeSlot, Long>, JpaSpecificationExecutor<ScheduleTimeSlot> {}
