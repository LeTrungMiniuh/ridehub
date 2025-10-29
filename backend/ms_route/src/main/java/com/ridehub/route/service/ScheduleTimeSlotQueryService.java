package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.ScheduleTimeSlot;
import com.ridehub.route.repository.ScheduleTimeSlotRepository;
import com.ridehub.route.service.criteria.ScheduleTimeSlotCriteria;
import com.ridehub.route.service.dto.ScheduleTimeSlotDTO;
import com.ridehub.route.service.mapper.ScheduleTimeSlotMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ScheduleTimeSlot} entities in the database.
 * The main input is a {@link ScheduleTimeSlotCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScheduleTimeSlotDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScheduleTimeSlotQueryService extends QueryService<ScheduleTimeSlot> {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTimeSlotQueryService.class);

    private final ScheduleTimeSlotRepository scheduleTimeSlotRepository;

    private final ScheduleTimeSlotMapper scheduleTimeSlotMapper;

    public ScheduleTimeSlotQueryService(
        ScheduleTimeSlotRepository scheduleTimeSlotRepository,
        ScheduleTimeSlotMapper scheduleTimeSlotMapper
    ) {
        this.scheduleTimeSlotRepository = scheduleTimeSlotRepository;
        this.scheduleTimeSlotMapper = scheduleTimeSlotMapper;
    }

    /**
     * Return a {@link List} of {@link ScheduleTimeSlotDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScheduleTimeSlotDTO> findByCriteria(ScheduleTimeSlotCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ScheduleTimeSlot> specification = createSpecification(criteria);
        return scheduleTimeSlotMapper.toDto(scheduleTimeSlotRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScheduleTimeSlotCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ScheduleTimeSlot> specification = createSpecification(criteria);
        return scheduleTimeSlotRepository.count(specification);
    }

    /**
     * Function to convert {@link ScheduleTimeSlotCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScheduleTimeSlot> createSpecification(ScheduleTimeSlotCriteria criteria) {
        Specification<ScheduleTimeSlot> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ScheduleTimeSlot_.id),
                buildStringSpecification(criteria.getSlotCode(), ScheduleTimeSlot_.slotCode),
                buildRangeSpecification(criteria.getDepartureTime(), ScheduleTimeSlot_.departureTime),
                buildRangeSpecification(criteria.getArrivalTime(), ScheduleTimeSlot_.arrivalTime),
                buildRangeSpecification(criteria.getBufferMinutes(), ScheduleTimeSlot_.bufferMinutes),
                buildRangeSpecification(criteria.getSequence(), ScheduleTimeSlot_.sequence),
                buildSpecification(criteria.getActive(), ScheduleTimeSlot_.active),
                buildRangeSpecification(criteria.getCreatedAt(), ScheduleTimeSlot_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), ScheduleTimeSlot_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), ScheduleTimeSlot_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), ScheduleTimeSlot_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), ScheduleTimeSlot_.deletedBy),
                buildSpecification(criteria.getScheduleId(), root -> root.join(ScheduleTimeSlot_.schedule, JoinType.LEFT).get(Schedule_.id))
            );
        }
        return specification;
    }
}
