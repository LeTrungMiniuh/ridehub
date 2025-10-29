package com.ridehub.booking.service;

import com.ridehub.booking.domain.*; // for static metamodels
import com.ridehub.booking.domain.PricingSnapshot;
import com.ridehub.booking.repository.PricingSnapshotRepository;
import com.ridehub.booking.service.criteria.PricingSnapshotCriteria;
import com.ridehub.booking.service.dto.PricingSnapshotDTO;
import com.ridehub.booking.service.mapper.PricingSnapshotMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PricingSnapshot} entities in the database.
 * The main input is a {@link PricingSnapshotCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PricingSnapshotDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PricingSnapshotQueryService extends QueryService<PricingSnapshot> {

    private static final Logger LOG = LoggerFactory.getLogger(PricingSnapshotQueryService.class);

    private final PricingSnapshotRepository pricingSnapshotRepository;

    private final PricingSnapshotMapper pricingSnapshotMapper;

    public PricingSnapshotQueryService(PricingSnapshotRepository pricingSnapshotRepository, PricingSnapshotMapper pricingSnapshotMapper) {
        this.pricingSnapshotRepository = pricingSnapshotRepository;
        this.pricingSnapshotMapper = pricingSnapshotMapper;
    }

    /**
     * Return a {@link List} of {@link PricingSnapshotDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PricingSnapshotDTO> findByCriteria(PricingSnapshotCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<PricingSnapshot> specification = createSpecification(criteria);
        return pricingSnapshotMapper.toDto(pricingSnapshotRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PricingSnapshotCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PricingSnapshot> specification = createSpecification(criteria);
        return pricingSnapshotRepository.count(specification);
    }

    /**
     * Function to convert {@link PricingSnapshotCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PricingSnapshot> createSpecification(PricingSnapshotCriteria criteria) {
        Specification<PricingSnapshot> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), PricingSnapshot_.id),
                buildRangeSpecification(criteria.getBaseFare(), PricingSnapshot_.baseFare),
                buildRangeSpecification(criteria.getVehicleFactor(), PricingSnapshot_.vehicleFactor),
                buildRangeSpecification(criteria.getFloorFactor(), PricingSnapshot_.floorFactor),
                buildRangeSpecification(criteria.getSeatFactor(), PricingSnapshot_.seatFactor),
                buildRangeSpecification(criteria.getScheduleOccasionFactor(), PricingSnapshot_.scheduleOccasionFactor),
                buildRangeSpecification(criteria.getFinalPrice(), PricingSnapshot_.finalPrice),
                buildRangeSpecification(criteria.getCreatedAt(), PricingSnapshot_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), PricingSnapshot_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), PricingSnapshot_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), PricingSnapshot_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), PricingSnapshot_.deletedBy),
                buildSpecification(criteria.getBookingId(), root -> root.join(PricingSnapshot_.booking, JoinType.LEFT).get(Booking_.id))
            );
        }
        return specification;
    }
}
