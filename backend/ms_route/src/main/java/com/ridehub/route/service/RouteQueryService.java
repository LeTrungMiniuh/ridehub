package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.repository.RouteRepository;
import com.ridehub.route.repository.search.RouteSearchRepository;
import com.ridehub.route.service.criteria.RouteCriteria;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.mapper.RouteMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Route} entities in the
 * database.
 * The main input is a {@link RouteCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RouteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RouteQueryService extends QueryService<Route> {

        private static final Logger LOG = LoggerFactory.getLogger(RouteQueryService.class);

        private final RouteRepository routeRepository;

        private final RouteMapper routeMapper;

        private final RouteSearchRepository routeSearchRepository;

        @PersistenceContext
        private EntityManager entityManager;

        public RouteQueryService(RouteRepository routeRepository, RouteMapper routeMapper,
                        RouteSearchRepository routeSearchRepository) {
                this.routeRepository = routeRepository;
                this.routeMapper = routeMapper;
                this.routeSearchRepository = routeSearchRepository;
        }

        /**
         * Return a {@link Page} of {@link RouteDTO} which matches the criteria from the
         * database.
         * 
         * @param criteria The object which holds all the filters, which the entities
         *                 should match.
         * @param page     The page, which should be returned.
         * @return the matching entities.
         */
        @Transactional(readOnly = true)
        public Page<RouteDTO> findByCriteria(RouteCriteria criteria, Pageable page) {
                LOG.debug("find by criteria : {}, page: {}", criteria, page);
                final Specification<Route> specification = createSpecification(criteria);
                return routeRepository.findAll(specification, page).map(routeMapper::toDto);
        }

        /**
         * Return the number of matching entities in the database.
         * 
         * @param criteria The object which holds all the filters, which the entities
         *                 should match.
         * @return the number of matching entities.
         */
        @Transactional(readOnly = true)
        public long countByCriteria(RouteCriteria criteria) {
                LOG.debug("count by criteria : {}", criteria);
                final Specification<Route> specification = createSpecification(criteria);
                return routeRepository.count(specification);
        }

        // In RouteQueryService (or a dedicated service), using your RouteRepository
        // extends JpaSpecificationExecutor<Route>
        @Transactional(readOnly = true)
        public Map<Long, Long> getRouteCountsByStationIds(List<Long> stationIds) {
                if (stationIds == null || stationIds.isEmpty())
                        return Map.of();

                Map<Long, Long> counts = new HashMap<>();

                // ---- 1) GROUP BY origin.id
                {
                        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
                        Root<Route> r = cq.from(Route.class);

                        cq.multiselect(
                                        r.get(Route_.origin).get(Station_.id).alias("stationId"),
                                        cb.countDistinct(r.get(Route_.id)).alias("cnt"));
                        cq.where(r.get(Route_.origin).get(Station_.id).in(stationIds));
                        cq.groupBy(r.get(Route_.origin).get(Station_.id));

                        for (Tuple t : entityManager.createQuery(cq).getResultList()) {
                                Long sid = (Long) t.get("stationId");
                                Long c = ((Number) t.get("cnt")).longValue();
                                counts.merge(sid, c, Long::sum);
                        }
                }

                // ---- 2) GROUP BY destination.id
                {
                        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
                        Root<Route> r = cq.from(Route.class);

                        cq.multiselect(
                                        r.get(Route_.destination).get(Station_.id).alias("stationId"),
                                        cb.countDistinct(r.get(Route_.id)).alias("cnt"));
                        cq.where(r.get(Route_.destination).get(Station_.id).in(stationIds));
                        cq.groupBy(r.get(Route_.destination).get(Station_.id));

                        for (Tuple t : entityManager.createQuery(cq).getResultList()) {
                                Long sid = (Long) t.get("stationId");
                                Long c = ((Number) t.get("cnt")).longValue();
                                counts.merge(sid, c, Long::sum);
                        }
                }

                // Ensure every requested id appears (even if zero)
                stationIds.forEach(id -> counts.putIfAbsent(id, 0L));
                return counts;
        }

        @Transactional(readOnly = true)
        public Page<Route> findByOriginOrDestination(Station station, Pageable pageable) {
                if (station == null)
                        return Page.empty(pageable);

                // Build criteria: stationId = station.getId() (matches origin OR destination
                // inside createSpecification)
                RouteCriteria criteria = new RouteCriteria();
                LongFilter stationId = new LongFilter();
                stationId.setEquals(station.getId());
                criteria.setStationId(stationId);

                // Reuse your unified spec (includes your fetch-spec that only runs on entity
                // query)
                Specification<Route> spec = createSpecification(criteria);

                return routeRepository.findAll(spec, pageable);
        }

        /**
         * Function to convert {@link RouteCriteria} to a {@link Specification}
         * 
         * @param criteria The object which holds all the filters, which the entities
         *                 should match.
         * @return the matching {@link Specification} of the entity.
         */
        protected Specification<Route> createSpecification(RouteCriteria criteria) {
                Specification<Route> specification = Specification.where(null);

                // ---- core filters (yours) ----
                if (criteria != null) {
                        specification = Specification.allOf(
                                        Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct())
                                                        : null,
                                        buildRangeSpecification(criteria.getId(), Route_.id),
                                        buildStringSpecification(criteria.getRouteCode(), Route_.routeCode),
                                        buildRangeSpecification(criteria.getDistanceKm(), Route_.distanceKm),
                                        buildRangeSpecification(criteria.getBaseFare(), Route_.baseFare),
                                        buildRangeSpecification(criteria.getCreatedAt(), Route_.createdAt),
                                        buildRangeSpecification(criteria.getUpdatedAt(), Route_.updatedAt),
                                        buildSpecification(criteria.getIsDeleted(), Route_.isDeleted),
                                        buildRangeSpecification(criteria.getDeletedAt(), Route_.deletedAt),
                                        buildSpecification(criteria.getDeletedBy(), Route_.deletedBy),
                                        buildSpecification(criteria.getOriginId(),
                                                        root -> root.join(Route_.origin, JoinType.LEFT)
                                                                        .get(Station_.id)),
                                        buildSpecification(criteria.getDestinationId(),
                                                        root -> root.join(Route_.destination, JoinType.LEFT)
                                                                        .get(Station_.id)),
                                        buildSpecification(criteria.getSchedulesId(), root -> root
                                                        .join(Route_.schedules, JoinType.LEFT).get(Schedule_.id)));

                        // origin.districtCode
                        if (criteria.getOriginDistrictCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getOriginDistrictCode(),
                                                                root -> root.join(Route_.origin, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .get(District_.districtCode)));
                        }

                        // origin.provinceCode
                        if (criteria.getOriginProvinceCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getOriginProvinceCode(),
                                                                root -> root.join(Route_.origin, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .join(District_.province, JoinType.LEFT)
                                                                                .get(Province_.provinceCode)));
                        }

                        // destination.districtCode
                        if (criteria.getDestinationDistrictCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getDestinationDistrictCode(),
                                                                root -> root.join(Route_.destination, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .get(District_.districtCode)));
                        }

                        // destination.provinceCode
                        if (criteria.getDestinationProvinceCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getDestinationProvinceCode(),
                                                                root -> root.join(Route_.destination, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .join(District_.province, JoinType.LEFT)
                                                                                .get(Province_.provinceCode)));
                        }

                        // (optional) stationId matches origin OR destination
                        if (criteria.getStationId() != null && criteria.getStationId().getEquals() != null) {
                                Long stationId = criteria.getStationId().getEquals();
                                specification = specification.and((root, query, cb) -> {
                                        var originJoin = root.join(Route_.origin, JoinType.LEFT);
                                        var destJoin = root.join(Route_.destination, JoinType.LEFT);
                                        var p1 = cb.equal(originJoin.get(Station_.id), stationId);
                                        var p2 = cb.equal(destJoin.get(Station_.id), stationId);
                                        // Important to avoid dup rows when OR-ing joins:
                                        if (Route.class.equals(query.getResultType()))
                                                query.distinct(true);
                                        return cb.or(p1, p2);
                                });
                        }
                }

                // ---- fetch spec (ONLY on entity query, never on count) ----
                Specification<Route> fetchSpec = (root, query, cb) -> {
                        Class<?> rt = query.getResultType();
                        if (rt != Long.class && rt != long.class) {
                                var o = root.fetch(Route_.origin, JoinType.LEFT);
                                var oa = o.fetch(Station_.address, JoinType.LEFT);
                                var ow = oa.fetch(Address_.ward, JoinType.LEFT);
                                var od = ow.fetch(Ward_.district, JoinType.LEFT);
                                od.fetch(District_.province, JoinType.LEFT);

                                var d = root.fetch(Route_.destination, JoinType.LEFT);
                                var da = d.fetch(Station_.address, JoinType.LEFT);
                                var dw = da.fetch(Address_.ward, JoinType.LEFT);
                                var dd = dw.fetch(Ward_.district, JoinType.LEFT);
                                dd.fetch(District_.province, JoinType.LEFT);

                                query.distinct(true); // guard against row explosion
                        }
                        return null;
                };

                return specification.and(fetchSpec);
        }

}
