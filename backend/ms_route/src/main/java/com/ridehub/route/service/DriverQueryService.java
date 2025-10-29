package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.Driver;
import com.ridehub.route.repository.DriverRepository;
import com.ridehub.route.service.criteria.DriverCriteria;
import com.ridehub.route.service.dto.DriverDTO;
import com.ridehub.route.service.mapper.DriverMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Driver} entities in the database.
 * The main input is a {@link DriverCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DriverDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DriverQueryService extends QueryService<Driver> {

    private static final Logger LOG = LoggerFactory.getLogger(DriverQueryService.class);

    private final DriverRepository driverRepository;

    private final DriverMapper driverMapper;

    public DriverQueryService(DriverRepository driverRepository, DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
    }

    /**
     * Return a {@link List} of {@link DriverDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DriverDTO> findByCriteria(DriverCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Driver> specification = createSpecification(criteria);
        return driverMapper.toDto(driverRepository.findAll(specification));
    }

    /**
     * Return number of matching entities in database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DriverCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Driver> specification = createSpecification(criteria);
        return driverRepository.count(specification);
    }

    /**
     * Find driver entities by criteria.
     * 
     * @param criteria The object which holds all the filters, which entities should match.
     * @return list of driver entities.
     */
    @Transactional(readOnly = true)
    public List<Driver> findEntitiesByCriteria(DriverCriteria criteria) {
        LOG.debug("find entities by criteria : {}", criteria);
        final Specification<Driver> specification = createSpecification(criteria);
        return driverRepository.findAll(specification);
    }

    /**
     * Function to convert {@link DriverCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Driver> createSpecification(DriverCriteria criteria) {
        Specification<Driver> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Driver_.id),
                buildStringSpecification(criteria.getLicenseClass(), Driver_.licenseClass),
                buildRangeSpecification(criteria.getYearsExperience(), Driver_.yearsExperience),
                buildRangeSpecification(criteria.getCreatedAt(), Driver_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Driver_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), Driver_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), Driver_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), Driver_.deletedBy),
                buildSpecification(criteria.getStaffId(), root -> root.join(Driver_.staff, JoinType.LEFT).get(Staff_.id))
            );
        }
        return specification;
    }
}
