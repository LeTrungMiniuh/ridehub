package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.Schedule;
import com.ridehub.route.repository.ScheduleRepository;
import com.ridehub.route.service.criteria.ScheduleCriteria;
import com.ridehub.route.service.dto.ScheduleDTO;
import com.ridehub.route.service.mapper.ScheduleMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Schedule} entities in the database.
 * The main input is a {@link ScheduleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScheduleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScheduleQueryService extends QueryService<Schedule> {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleQueryService.class);

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleQueryService(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    /**
     * Return a {@link List} of {@link ScheduleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> findByCriteria(ScheduleCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleMapper.toDto(scheduleRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScheduleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleRepository.count(specification);
    }

    /**
     * Function to convert {@link ScheduleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Schedule> createSpecification(ScheduleCriteria criteria) {
        Specification<Schedule> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Schedule_.id),
                buildStringSpecification(criteria.getScheduleCode(), Schedule_.scheduleCode),
                buildRangeSpecification(criteria.getStartDate(), Schedule_.startDate),
                buildRangeSpecification(criteria.getEndDate(), Schedule_.endDate),
                buildStringSpecification(criteria.getDaysOfWeek(), Schedule_.daysOfWeek),
                buildSpecification(criteria.getActive(), Schedule_.active),
                buildRangeSpecification(criteria.getCreatedAt(), Schedule_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Schedule_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), Schedule_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), Schedule_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), Schedule_.deletedBy),
                buildSpecification(criteria.getTimeSlotsId(), root ->
                    root.join(Schedule_.timeSlots, JoinType.LEFT).get(ScheduleTimeSlot_.id)
                ),
                buildSpecification(criteria.getOccasionRuleId(), root ->
                    root.join(Schedule_.occasionRule, JoinType.LEFT).get(ScheduleOccasion_.id)
                ),
                buildSpecification(criteria.getRouteId(), root -> root.join(Schedule_.route, JoinType.LEFT).get(Route_.id))
            );
        }
        return specification;
    }
}
