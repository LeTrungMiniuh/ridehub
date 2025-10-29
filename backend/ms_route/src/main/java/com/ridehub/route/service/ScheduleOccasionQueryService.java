package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.ScheduleOccasion;
import com.ridehub.route.repository.ScheduleOccasionRepository;
import com.ridehub.route.service.criteria.ScheduleOccasionCriteria;
import com.ridehub.route.service.dto.ScheduleOccasionDTO;
import com.ridehub.route.service.mapper.ScheduleOccasionMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ScheduleOccasion} entities in the database.
 * The main input is a {@link ScheduleOccasionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScheduleOccasionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScheduleOccasionQueryService extends QueryService<ScheduleOccasion> {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleOccasionQueryService.class);

    private final ScheduleOccasionRepository scheduleOccasionRepository;

    private final ScheduleOccasionMapper scheduleOccasionMapper;

    public ScheduleOccasionQueryService(
        ScheduleOccasionRepository scheduleOccasionRepository,
        ScheduleOccasionMapper scheduleOccasionMapper
    ) {
        this.scheduleOccasionRepository = scheduleOccasionRepository;
        this.scheduleOccasionMapper = scheduleOccasionMapper;
    }

    /**
     * Return a {@link List} of {@link ScheduleOccasionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScheduleOccasionDTO> findByCriteria(ScheduleOccasionCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ScheduleOccasion> specification = createSpecification(criteria);
        return scheduleOccasionMapper.toDto(scheduleOccasionRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScheduleOccasionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ScheduleOccasion> specification = createSpecification(criteria);
        return scheduleOccasionRepository.count(specification);
    }

    /**
     * Function to convert {@link ScheduleOccasionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScheduleOccasion> createSpecification(ScheduleOccasionCriteria criteria) {
        Specification<ScheduleOccasion> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ScheduleOccasion_.id),
                buildSpecification(criteria.getOccasion(), ScheduleOccasion_.occasion),
                buildRangeSpecification(criteria.getOccasionFactor(), ScheduleOccasion_.occasionFactor),
                buildRangeSpecification(criteria.getCreatedAt(), ScheduleOccasion_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), ScheduleOccasion_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), ScheduleOccasion_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), ScheduleOccasion_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), ScheduleOccasion_.deletedBy),
                buildSpecification(criteria.getScheduleId(), root -> root.join(ScheduleOccasion_.schedules, JoinType.LEFT).get(Schedule_.id)
                )
            );
        }
        return specification;
    }
}
