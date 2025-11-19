package com.ridehub.user.service;

import com.ridehub.user.domain.*; // for static metamodels
import com.ridehub.user.domain.UserStatistics;
import com.ridehub.user.repository.UserStatisticsRepository;
import com.ridehub.user.service.criteria.UserStatisticsCriteria;
import com.ridehub.user.service.dto.UserStatisticsDTO;
import com.ridehub.user.service.mapper.UserStatisticsMapper;
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
 * Service for executing complex queries for {@link UserStatistics} entities in the database.
 * The main input is a {@link UserStatisticsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link UserStatisticsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserStatisticsQueryService extends QueryService<UserStatistics> {

    private static final Logger LOG = LoggerFactory.getLogger(UserStatisticsQueryService.class);

    private final UserStatisticsRepository userStatisticsRepository;

    private final UserStatisticsMapper userStatisticsMapper;

    public UserStatisticsQueryService(UserStatisticsRepository userStatisticsRepository, UserStatisticsMapper userStatisticsMapper) {
        this.userStatisticsRepository = userStatisticsRepository;
        this.userStatisticsMapper = userStatisticsMapper;
    }

    /**
     * Return a {@link Page} of {@link UserStatisticsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserStatisticsDTO> findByCriteria(UserStatisticsCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserStatistics> specification = createSpecification(criteria);
        return userStatisticsRepository.findAll(specification, page).map(userStatisticsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserStatisticsCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UserStatistics> specification = createSpecification(criteria);
        return userStatisticsRepository.count(specification);
    }

    /**
     * Function to convert {@link UserStatisticsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserStatistics> createSpecification(UserStatisticsCriteria criteria) {
        Specification<UserStatistics> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), UserStatistics_.id),
                buildRangeSpecification(criteria.getTotalTrips(), UserStatistics_.totalTrips),
                buildRangeSpecification(criteria.getTotalSpent(), UserStatistics_.totalSpent),
                buildStringSpecification(criteria.getFavoriteRoutes(), UserStatistics_.favoriteRoutes),
                buildStringSpecification(criteria.getPreferredVehicleTypes(), UserStatistics_.preferredVehicleTypes),
                buildRangeSpecification(criteria.getAverageTripDuration(), UserStatistics_.averageTripDuration),
                buildRangeSpecification(criteria.getLastTravelDate(), UserStatistics_.lastTravelDate),
                buildStringSpecification(criteria.getBookingFrequency(), UserStatistics_.bookingFrequency),
                buildRangeSpecification(criteria.getLoyaltyPoints(), UserStatistics_.loyaltyPoints),
                buildStringSpecification(criteria.getMostFrequentOrigin(), UserStatistics_.mostFrequentOrigin),
                buildStringSpecification(criteria.getMostFrequentDestination(), UserStatistics_.mostFrequentDestination),
                buildRangeSpecification(criteria.getAverageTripDistance(), UserStatistics_.averageTripDistance),
                buildStringSpecification(criteria.getPeakTravelTime(), UserStatistics_.peakTravelTime),
                buildRangeSpecification(criteria.getWeekendTrips(), UserStatistics_.weekendTrips),
                buildRangeSpecification(criteria.getHolidayTrips(), UserStatistics_.holidayTrips),
                buildRangeSpecification(criteria.getCancelledTrips(), UserStatistics_.cancelledTrips),
                buildRangeSpecification(criteria.getOnTimePerformanceRate(), UserStatistics_.onTimePerformanceRate),
                buildStringSpecification(criteria.getPreferredSeatTypes(), UserStatistics_.preferredSeatTypes),
                buildRangeSpecification(criteria.getMonthlyTripCount(), UserStatistics_.monthlyTripCount),
                buildRangeSpecification(criteria.getCreatedAt(), UserStatistics_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), UserStatistics_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), UserStatistics_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), UserStatistics_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), UserStatistics_.deletedBy),
                buildSpecification(criteria.getUserId(), root -> root.join(UserStatistics_.user, JoinType.LEFT).get(AppUser_.id))
            );
        }
        return specification;
    }
}
