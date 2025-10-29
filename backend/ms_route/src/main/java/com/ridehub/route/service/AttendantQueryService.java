package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.Attendant;
import com.ridehub.route.repository.AttendantRepository;
import com.ridehub.route.service.criteria.AttendantCriteria;
import com.ridehub.route.service.dto.AttendantDTO;
import com.ridehub.route.service.mapper.AttendantMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Attendant} entities in the database.
 * The main input is a {@link AttendantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AttendantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AttendantQueryService extends QueryService<Attendant> {

    private static final Logger LOG = LoggerFactory.getLogger(AttendantQueryService.class);

    private final AttendantRepository attendantRepository;

    private final AttendantMapper attendantMapper;

    public AttendantQueryService(AttendantRepository attendantRepository, AttendantMapper attendantMapper) {
        this.attendantRepository = attendantRepository;
        this.attendantMapper = attendantMapper;
    }

    /**
     * Return a {@link List} of {@link AttendantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AttendantDTO> findByCriteria(AttendantCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Attendant> specification = createSpecification(criteria);
        return attendantMapper.toDto(attendantRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AttendantCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Attendant> specification = createSpecification(criteria);
        return attendantRepository.count(specification);
    }

    /**
     * Function to convert {@link AttendantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Attendant> createSpecification(AttendantCriteria criteria) {
        Specification<Attendant> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Attendant_.id),
                buildRangeSpecification(criteria.getCreatedAt(), Attendant_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Attendant_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), Attendant_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), Attendant_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), Attendant_.deletedBy),
                buildSpecification(criteria.getStaffId(), root -> root.join(Attendant_.staff, JoinType.LEFT).get(Staff_.id))
            );
        }
        return specification;
    }

    /**
     * Find attendant entities by criteria.
     * 
     * @param criteria The object which holds all the filters, which entities should match.
     * @return list of attendant entities.
     */
    @Transactional(readOnly = true)
    public List<Attendant> findEntitiesByCriteria(AttendantCriteria criteria) {
        LOG.debug("find entities by criteria : {}", criteria);
        final Specification<Attendant> specification = createSpecification(criteria);
        return attendantRepository.findAll(specification);
    }
}
