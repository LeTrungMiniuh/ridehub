package com.ridehub.user.repository;

import com.ridehub.user.domain.TripStatistics;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TripStatistics entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TripStatisticsRepository extends JpaRepository<TripStatistics, Long>, JpaSpecificationExecutor<TripStatistics> {}
