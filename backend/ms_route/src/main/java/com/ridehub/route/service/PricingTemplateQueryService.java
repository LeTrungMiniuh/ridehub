package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.PricingTemplate;
import com.ridehub.route.repository.PricingTemplateRepository;
import com.ridehub.route.service.criteria.PricingTemplateCriteria;
import com.ridehub.route.service.dto.PricingTemplateDTO;
import com.ridehub.route.service.mapper.PricingTemplateMapper;
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
 * Service for executing complex queries for {@link PricingTemplate} entities in the database.
 * The main input is a {@link PricingTemplateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PricingTemplateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PricingTemplateQueryService extends QueryService<PricingTemplate> {

    private static final Logger LOG = LoggerFactory.getLogger(PricingTemplateQueryService.class);

    private final PricingTemplateRepository pricingTemplateRepository;

    private final PricingTemplateMapper pricingTemplateMapper;

    public PricingTemplateQueryService(PricingTemplateRepository pricingTemplateRepository, PricingTemplateMapper pricingTemplateMapper) {
        this.pricingTemplateRepository = pricingTemplateRepository;
        this.pricingTemplateMapper = pricingTemplateMapper;
    }

    /**
     * Return a {@link Page} of {@link PricingTemplateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PricingTemplateDTO> findByCriteria(PricingTemplateCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PricingTemplate> specification = createSpecification(criteria);
        return pricingTemplateRepository.findAll(specification, page).map(pricingTemplateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PricingTemplateCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PricingTemplate> specification = createSpecification(criteria);
        return pricingTemplateRepository.count(specification);
    }

    /**
     * Function to convert {@link PricingTemplateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PricingTemplate> createSpecification(PricingTemplateCriteria criteria) {
        Specification<PricingTemplate> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), PricingTemplate_.id),
                buildSpecification(criteria.getVehicleType(), PricingTemplate_.vehicleType),
                buildSpecification(criteria.getSeatType(), PricingTemplate_.seatType),
                buildSpecification(criteria.getOccasionType(), PricingTemplate_.occasionType),
                buildRangeSpecification(criteria.getBaseFare(), PricingTemplate_.baseFare),
                buildRangeSpecification(criteria.getVehicleFactor(), PricingTemplate_.vehicleFactor),
                buildRangeSpecification(criteria.getFloorFactor(), PricingTemplate_.floorFactor),
                buildRangeSpecification(criteria.getSeatFactor(), PricingTemplate_.seatFactor),
                buildRangeSpecification(criteria.getOccasionFactor(), PricingTemplate_.occasionFactor),
                buildRangeSpecification(criteria.getFinalPrice(), PricingTemplate_.finalPrice),
                buildRangeSpecification(criteria.getValidFrom(), PricingTemplate_.validFrom),
                buildRangeSpecification(criteria.getValidTo(), PricingTemplate_.validTo),
                buildRangeSpecification(criteria.getCreatedAt(), PricingTemplate_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), PricingTemplate_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), PricingTemplate_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), PricingTemplate_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), PricingTemplate_.deletedBy),
                buildSpecification(criteria.getRouteId(), root -> root.join(PricingTemplate_.route, JoinType.LEFT).get(Route_.id))
            );
        }
        return specification;
    }
}
