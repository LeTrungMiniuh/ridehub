package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.repository.FloorRepository;
import com.ridehub.route.service.criteria.FloorCriteria;
import com.ridehub.route.service.criteria.SeatCriteria;
import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.mapper.FloorMapper;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Floor} entities in the
 * database.
 * The main input is a {@link FloorCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FloorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FloorQueryService extends QueryService<Floor> {

    private static final Logger LOG = LoggerFactory.getLogger(FloorQueryService.class);

    private final FloorRepository floorRepository;

    private final FloorMapper floorMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public FloorQueryService(FloorRepository floorRepository, FloorMapper floorMapper) {
        this.floorRepository = floorRepository;
        this.floorMapper = floorMapper;
    }

    /**
     * Return a {@link List} of {@link FloorDTO} which matches the criteria from the
     * database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FloorDTO> findByCriteria(FloorCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Floor> specification = createSpecification(criteria);
        return floorMapper.toDto(floorRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FloorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Floor> specification = createSpecification(criteria);
        return floorRepository.count(specification);
    }

    @Transactional(readOnly = true)
    public List<FloorDTO> findFloorsBySeatMapId(Long seatMapId) {
        if (seatMapId == null)
            return List.of();

        FloorCriteria criteria = new FloorCriteria();
        LongFilter seatMapIdFilter = new LongFilter();
        seatMapIdFilter.setEquals(seatMapId); // single value, not .in(...)
        criteria.setSeatMapId(seatMapIdFilter); // filter is on seatMapId, not floorId

        return findByCriteria(criteria); // your existing FloorQueryService.findByCriteria
    }

    /**
     * Find floor factors by seatMap ID using Criteria API.
     * Returns Map<Integer, BigDecimal> where key is floor number and value is price factor.
     */
    @Transactional(readOnly = true)
    public Map<Integer, BigDecimal> findFloorFactorsBySeatMapId(Long seatMapId) {
        if (seatMapId == null) {
            return Map.of();
        }

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createTupleQuery();

        // Roots and joins
        var floor = cq.from(Floor.class);
        var seatMap = floor.join(Floor_.seatMap, JoinType.INNER);

        cq.multiselect(
                floor.get(Floor_.floorNo),
                floor.get(Floor_.priceFactorFloor)
        );

        cq.where(cb.and(
                cb.equal(seatMap.get(SeatMap_.id), seatMapId),
                cb.or(cb.isFalse(floor.get(Floor_.isDeleted)), cb.isNull(floor.get(Floor_.isDeleted))),
                cb.or(cb.isFalse(seatMap.get(SeatMap_.isDeleted)), cb.isNull(seatMap.get(SeatMap_.isDeleted)))
        ));

        List<Tuple> results = entityManager.createQuery(cq).getResultList();
        
        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> (Integer) tuple.get(0),
                        tuple -> (BigDecimal) tuple.get(1),
                        (existing, replacement) -> existing
                ));
    }

    /**
     * Function to convert {@link FloorCriteria} to a {@link Specification}
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Floor> createSpecification(FloorCriteria criteria) {
        Specification<Floor> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                    buildRangeSpecification(criteria.getId(), Floor_.id),
                    buildRangeSpecification(criteria.getFloorNo(), Floor_.floorNo),
                    buildRangeSpecification(criteria.getPriceFactorFloor(), Floor_.priceFactorFloor),
                    buildRangeSpecification(criteria.getCreatedAt(), Floor_.createdAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), Floor_.updatedAt),
                    buildSpecification(criteria.getIsDeleted(), Floor_.isDeleted),
                    buildRangeSpecification(criteria.getDeletedAt(), Floor_.deletedAt),
                    buildSpecification(criteria.getDeletedBy(), Floor_.deletedBy),
                    buildSpecification(criteria.getSeatMapId(),
                            root -> root.join(Floor_.seatMap, JoinType.LEFT).get(SeatMap_.id)));
        }
        return specification;
    }
}
