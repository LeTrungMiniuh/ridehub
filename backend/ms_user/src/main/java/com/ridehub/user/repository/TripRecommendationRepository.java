package com.ridehub.user.repository;

import com.ridehub.user.domain.TripRecommendation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TripRecommendation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TripRecommendationRepository
    extends JpaRepository<TripRecommendation, Long>, JpaSpecificationExecutor<TripRecommendation> {}
