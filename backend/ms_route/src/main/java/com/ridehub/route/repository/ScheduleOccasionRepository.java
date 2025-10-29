package com.ridehub.route.repository;

import com.ridehub.route.domain.ScheduleOccasion;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ScheduleOccasion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleOccasionRepository extends JpaRepository<ScheduleOccasion, Long>, JpaSpecificationExecutor<ScheduleOccasion> {}
