package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.enumeration.SeatType;
import com.ridehub.route.repository.SeatRepository;
import com.ridehub.route.service.criteria.SeatCriteria;
import com.ridehub.route.service.dto.SeatDTO;
import com.ridehub.route.service.mapper.SeatMapper;
import jakarta.persistence.criteria.Join;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.JoinType;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Seat} entities in the
 * database.
 * The main input is a {@link SeatCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SeatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SeatQueryService extends QueryService<Seat> {

    private static final Logger LOG = LoggerFactory.getLogger(SeatQueryService.class);

    private final SeatRepository seatRepository;

    private final SeatMapper seatMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public SeatQueryService(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }

    /**
     * Return a {@link List} of {@link SeatDTO} which matches the criteria from the
     * database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SeatDTO> findByCriteria(SeatCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Seat> specification = createSpecification(criteria);
        return seatMapper.toDto(seatRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SeatCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Seat> specification = createSpecification(criteria);
        return seatRepository.count(specification);
    }

    /**
     * Function to convert {@link SeatCriteria} to a {@link Specification}
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Seat> createSpecification(SeatCriteria criteria) {
        Specification<Seat> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                    buildRangeSpecification(criteria.getId(), Seat_.id),
                    buildStringSpecification(criteria.getSeatNo(), Seat_.seatNo),
                    buildRangeSpecification(criteria.getRowNo(), Seat_.rowNo),
                    buildRangeSpecification(criteria.getColNo(), Seat_.colNo),
                    buildRangeSpecification(criteria.getPriceFactor(), Seat_.priceFactor),
                    buildSpecification(criteria.getType(), Seat_.type),
                    buildRangeSpecification(criteria.getCreatedAt(), Seat_.createdAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), Seat_.updatedAt),
                    buildSpecification(criteria.getIsDeleted(), Seat_.isDeleted),
                    buildRangeSpecification(criteria.getDeletedAt(), Seat_.deletedAt),
                    buildSpecification(criteria.getDeletedBy(), Seat_.deletedBy),
                    buildSpecification(criteria.getFloorId(),
                            root -> root.join(Seat_.floor, JoinType.LEFT).get(Floor_.id)));
        }
        return specification;
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> findByFloorIds(List<Long> floorIds) {
        if (floorIds == null || floorIds.isEmpty())
            return List.of();

        SeatCriteria criteria = new SeatCriteria();
        LongFilter floorIdFilter = new LongFilter();
        floorIdFilter.setIn(floorIds.stream().toList());
        criteria.setFloorId(floorIdFilter);

        return findByCriteria(criteria);
    }

    /**
     * Count seats per vehicle for the given vehicle IDs using Criteria API.
     * Mirrors:
     * SELECT v.id, COUNT(s)
     * FROM Seat s
     * JOIN s.floor f
     * JOIN f.seatMap sm
     * JOIN sm.vehicle v
     * WHERE v.id IN (:vehicleIds)
     * AND (s.isDeleted = false OR s.isDeleted IS NULL)
     * AND (f.isDeleted = false OR f.isDeleted IS NULL)
     * AND (v.isDeleted = false OR v.isDeleted IS NULL)
     * GROUP BY v.id
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> countSeatsByVehicleIds(List<Long> vehicleIds) {
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            return Map.of();
        }

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createTupleQuery();

        // Roots / joins
        var seat = cq.from(Seat.class);
        var floor = seat.join(Seat_.floor, JoinType.INNER);
        var seatMap = floor.join(Floor_.seatMap, JoinType.INNER);

        // Vehicle is now a root; relate it via seatMap
        var vehicle = cq.from(Vehicle.class);

        cq.multiselect(
                vehicle.get(Vehicle_.id).alias("vehicleId"),
                cb.count(seat).alias("seatCount"));

        cq.where(cb.and(
                // link vehicle ↔ seatMap
                cb.equal(vehicle.get(Vehicle_.seatMap), seatMap),

                // filter by requested vehicles
                vehicle.get(Vehicle_.id).in(vehicleIds),

                // soft-delete guards
                cb.or(cb.isFalse(seat.get(Seat_.isDeleted)), cb.isNull(seat.get(Seat_.isDeleted))),
                cb.or(cb.isFalse(floor.get(Floor_.isDeleted)), cb.isNull(floor.get(Floor_.isDeleted))),
                cb.or(cb.isFalse(vehicle.get(Vehicle_.isDeleted)), cb.isNull(vehicle.get(Vehicle_.isDeleted)))));

        cq.groupBy(vehicle.get(Vehicle_.id));

        List<Tuple> rows = entityManager.createQuery(cq).getResultList();

        return rows.stream()
                .collect(Collectors.toMap(
                        t -> (Long) t.get("vehicleId"),
                        t -> (Long) t.get("seatCount")));
    }

    /**
     * Find seats by trip ID and seat numbers using Criteria API.
     * This joins through the floor -> seatMap -> vehicle relationship and then
     * filters by trip.
     */
    @Transactional(readOnly = true)
    public List<Seat> findByTripIdAndSeatNumbers(Long tripId, List<String> seatNumbers) {
        if (tripId == null || seatNumbers == null || seatNumbers.isEmpty())
            return List.of();

        // normalize once
        List<String> seatNos = seatNumbers.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toUpperCase())
                .toList();

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Seat.class);
        var seat = cq.from(Seat.class);

        var floor = seat.join(Seat_.floor, JoinType.INNER);
        var seatMap = floor.join(Floor_.seatMap, JoinType.INNER);

        // subquery: seatMapId of the vehicle used by this trip
        var sq = cq.subquery(Long.class);
        var t = sq.from(Trip.class);
        var v = t.join(Trip_.vehicle);
        var vSeatMap = v.join(Vehicle_.seatMap);
        sq.select(vSeatMap.get(SeatMap_.id))
                .where(cb.equal(t.get(Trip_.id), tripId));

        cq.select(seat).distinct(true);
        cq.where(cb.and(
                cb.equal(seatMap.get(SeatMap_.id), sq), // seatMap matches trip's vehicle seatMap
                cb.upper(seat.get(Seat_.seatNo)).in(seatNos), // seat numbers
                cb.or(cb.isFalse(seat.get(Seat_.isDeleted)), cb.isNull(seat.get(Seat_.isDeleted))),
                cb.or(cb.isFalse(floor.get(Floor_.isDeleted)), cb.isNull(floor.get(Floor_.isDeleted))),
                cb.or(cb.isFalse(seatMap.get(SeatMap_.isDeleted)), cb.isNull(seatMap.get(SeatMap_.isDeleted)))));

        return entityManager.createQuery(cq).getResultList();
    }

    /**
     * Find a single seat by trip ID and seat number using Criteria API.
     */
    @Transactional(readOnly = true)
    public Optional<Seat> findByTripIdAndSeatNumber(Long tripId, String seatNumber) {
        if (tripId == null || seatNumber == null || seatNumber.trim().isEmpty()) {
            return Optional.empty();
        }

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Seat.class);
        var seat = cq.from(Seat.class);

        // Join through floor -> seatMap
        var floor = seat.join(Seat_.floor, JoinType.INNER);
        var seatMap = floor.join(Floor_.seatMap, JoinType.INNER);

        // Subquery to get vehicle ID from trip
        var tripSubquery = cq.subquery(Long.class);
        var trip = tripSubquery.from(Trip.class);
        tripSubquery.select(trip.get(Trip_.vehicle).get(Vehicle_.id))
                .where(cb.equal(trip.get(Trip_.id), tripId));

        cq.where(cb.and(
                // Match vehicle from trip
                cb.equal(seatMap.get(SeatMap_.id), tripSubquery),

                // Match seat number
                cb.equal(seat.get(Seat_.seatNo), seatNumber),

                // Soft-delete guards
                cb.or(cb.isFalse(seat.get(Seat_.isDeleted)), cb.isNull(seat.get(Seat_.isDeleted))),
                cb.or(cb.isFalse(floor.get(Floor_.isDeleted)), cb.isNull(floor.get(Floor_.isDeleted))),
                cb.or(cb.isFalse(seatMap.get(SeatMap_.isDeleted)), cb.isNull(seatMap.get(SeatMap_.isDeleted)))));

        try {
            Seat result = entityManager.createQuery(cq).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Find distinct seat types by seatMap ID using Criteria API.
     * Single query to get all seat types for a vehicle's seat map.
     */
    @Transactional(readOnly = true)
    public Set<SeatType> findDistinctSeatTypesBySeatMapId(Long seatMapId) {
        if (seatMapId == null) {
            return Set.of();
        }

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createTupleQuery();

        // Roots and joins
        var seat = cq.from(Seat.class);
        var floor = seat.join(Seat_.floor, JoinType.INNER);
        var seatMap = floor.join(Floor_.seatMap, JoinType.INNER);

        cq.multiselect(seat.get(Seat_.type)).distinct(true);

        cq.where(cb.and(
                cb.equal(seatMap.get(SeatMap_.id), seatMapId),
                cb.or(cb.isFalse(seat.get(Seat_.isDeleted)), cb.isNull(seat.get(Seat_.isDeleted))),
                cb.or(cb.isFalse(floor.get(Floor_.isDeleted)), cb.isNull(floor.get(Floor_.isDeleted))),
                cb.or(cb.isFalse(seatMap.get(SeatMap_.isDeleted)), cb.isNull(seatMap.get(SeatMap_.isDeleted)))));

        List<Tuple> results = entityManager.createQuery(cq).getResultList();

        return results.stream()
                .map(tuple -> (SeatType) tuple.get(0))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Find seat types with their price factors by seatMap ID.
     * Returns Map<SeatType, BigDecimal> where value is the average price factor for
     * that seat type.
     */
    @Transactional(readOnly = true)
    public Map<SeatType, BigDecimal> findSeatTypesWithFactorsBySeatMapId(Long seatMapId) {
        if (seatMapId == null) {
            return Map.of();
        }

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createTupleQuery();

        // Roots and joins
        var seat = cq.from(Seat.class);
        var floor = seat.join(Seat_.floor, JoinType.INNER);
        var seatMap = floor.join(Floor_.seatMap, JoinType.INNER);

        cq.multiselect(
                seat.get(Seat_.type),
                cb.avg(seat.get(Seat_.priceFactor)));

        cq.where(cb.and(
                cb.equal(seatMap.get(SeatMap_.id), seatMapId),
                cb.or(cb.isFalse(seat.get(Seat_.isDeleted)), cb.isNull(seat.get(Seat_.isDeleted))),
                cb.or(cb.isFalse(floor.get(Floor_.isDeleted)), cb.isNull(floor.get(Floor_.isDeleted))),
                cb.or(cb.isFalse(seatMap.get(SeatMap_.isDeleted)), cb.isNull(seatMap.get(SeatMap_.isDeleted)))));

        cq.groupBy(seat.get(Seat_.type));

        List<Tuple> results = entityManager.createQuery(cq).getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> (SeatType) tuple.get(0),
                        tuple -> BigDecimal.valueOf(((Number) tuple.get(1)).doubleValue()),
                        (existing, replacement) -> existing));
    }

}