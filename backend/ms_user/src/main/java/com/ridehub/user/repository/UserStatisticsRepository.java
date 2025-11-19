package com.ridehub.user.repository;

import com.ridehub.user.domain.UserStatistics;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserStatistics entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long>, JpaSpecificationExecutor<UserStatistics> {}
