package com.ridehub.user.service;

import com.ridehub.user.domain.*; // for static metamodels
import com.ridehub.user.domain.TripStatistics;
import com.ridehub.user.repository.TripStatisticsRepository;
import com.ridehub.user.service.criteria.TripStatisticsCriteria;
import com.ridehub.user.service.dto.TripStatisticsDTO;
import com.ridehub.user.service.mapper.TripStatisticsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TripStatistics} entities in the database.
 * The main input is a {@link TripStatisticsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TripStatisticsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TripStatisticsQueryService extends QueryService<TripStatistics> {

    private static final Logger LOG = LoggerFactory.getLogger(TripStatisticsQueryService.class);

    private final TripStatisticsRepository tripStatisticsRepository;

    private final TripStatisticsMapper tripStatisticsMapper;

    public TripStatisticsQueryService(TripStatisticsRepository tripStatisticsRepository, TripStatisticsMapper tripStatisticsMapper) {
        this.tripStatisticsRepository = tripStatisticsRepository;
        this.tripStatisticsMapper = tripStatisticsMapper;
    }

    /**
     * Return a {@link Page} of {@link TripStatisticsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TripStatisticsDTO> findByCriteria(TripStatisticsCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TripStatistics> specification = createSpecification(criteria);
        return tripStatisticsRepository.findAll(specification, page).map(tripStatisticsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TripStatisticsCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TripStatistics> specification = createSpecification(criteria);
        return tripStatisticsRepository.count(specification);
    }

    /**
     * Function to convert {@link TripStatisticsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TripStatistics> createSpecification(TripStatisticsCriteria criteria) {
        Specification<TripStatistics> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TripStatistics_.id),
                buildRangeSpecification(criteria.getRouteId(), TripStatistics_.routeId),
                buildSpecification(criteria.getVehicleType(), TripStatistics_.vehicleType),
                buildSpecification(criteria.getOccasionType(), TripStatistics_.occasionType),
                buildRangeSpecification(criteria.getTotalBookings(), TripStatistics_.totalBookings),
                buildRangeSpecification(criteria.getTotalRevenue(), TripStatistics_.totalRevenue),
                buildRangeSpecification(criteria.getAveragePrice(), TripStatistics_.averagePrice),
                buildRangeSpecification(criteria.getOccupancyRate(), TripStatistics_.occupancyRate),
                buildStringSpecification(criteria.getPopularSeatTypes(), TripStatistics_.popularSeatTypes),
                buildStringSpecification(criteria.getPeakTravelTimes(), TripStatistics_.peakTravelTimes),
                buildRangeSpecification(criteria.getCancellationRate(), TripStatistics_.cancellationRate),
                buildRangeSpecification(criteria.getCustomerSatisfactionScore(), TripStatistics_.customerSatisfactionScore),
                buildStringSpecification(criteria.getMonthlyTrend(), TripStatistics_.monthlyTrend),
                buildRangeSpecification(criteria.getValidFrom(), TripStatistics_.validFrom),
                buildRangeSpecification(criteria.getValidTo(), TripStatistics_.validTo),
                buildRangeSpecification(criteria.getCreatedAt(), TripStatistics_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), TripStatistics_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), TripStatistics_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), TripStatistics_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), TripStatistics_.deletedBy)
            );
        }
        return specification;
    }
}
