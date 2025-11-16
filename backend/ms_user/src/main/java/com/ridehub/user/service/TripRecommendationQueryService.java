package com.ridehub.user.service;

import com.ridehub.user.domain.*; // for static metamodels
import com.ridehub.user.domain.TripRecommendation;
import com.ridehub.user.repository.TripRecommendationRepository;
import com.ridehub.user.service.criteria.TripRecommendationCriteria;
import com.ridehub.user.service.dto.TripRecommendationDTO;
import com.ridehub.user.service.mapper.TripRecommendationMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TripRecommendation} entities in the database.
 * The main input is a {@link TripRecommendationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TripRecommendationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TripRecommendationQueryService extends QueryService<TripRecommendation> {

    private static final Logger LOG = LoggerFactory.getLogger(TripRecommendationQueryService.class);

    private final TripRecommendationRepository tripRecommendationRepository;

    private final TripRecommendationMapper tripRecommendationMapper;

    public TripRecommendationQueryService(
        TripRecommendationRepository tripRecommendationRepository,
        TripRecommendationMapper tripRecommendationMapper
    ) {
        this.tripRecommendationRepository = tripRecommendationRepository;
        this.tripRecommendationMapper = tripRecommendationMapper;
    }

    /**
     * Return a {@link Page} of {@link TripRecommendationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TripRecommendationDTO> findByCriteria(TripRecommendationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TripRecommendation> specification = createSpecification(criteria);
        return tripRecommendationRepository.findAll(specification, page).map(tripRecommendationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TripRecommendationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TripRecommendation> specification = createSpecification(criteria);
        return tripRecommendationRepository.count(specification);
    }

    /**
     * Function to convert {@link TripRecommendationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TripRecommendation> createSpecification(TripRecommendationCriteria criteria) {
        Specification<TripRecommendation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TripRecommendation_.id),
                buildStringSpecification(criteria.getOrigin(), TripRecommendation_.origin),
                buildStringSpecification(criteria.getDestination(), TripRecommendation_.destination),
                buildRangeSpecification(criteria.getTravelDate(), TripRecommendation_.travelDate),
                buildRangeSpecification(criteria.getPreferredTime(), TripRecommendation_.preferredTime),
                buildStringSpecification(criteria.getBudgetRange(), TripRecommendation_.budgetRange),
                buildStringSpecification(criteria.getSeatPreference(), TripRecommendation_.seatPreference),
                buildStringSpecification(criteria.getRecommendedTrips(), TripRecommendation_.recommendedTrips),
                buildRangeSpecification(criteria.getConfidenceScore(), TripRecommendation_.confidenceScore),
                buildSpecification(criteria.getIsBooked(), TripRecommendation_.isBooked),
                buildRangeSpecification(criteria.getFeedbackRating(), TripRecommendation_.feedbackRating),
                buildRangeSpecification(criteria.getCreatedAt(), TripRecommendation_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), TripRecommendation_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), TripRecommendation_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), TripRecommendation_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), TripRecommendation_.deletedBy),
                buildSpecification(criteria.getUserId(), root -> root.join(TripRecommendation_.user, JoinType.LEFT).get(AppUser_.id))
            );
        }
        return specification;
    }
}
