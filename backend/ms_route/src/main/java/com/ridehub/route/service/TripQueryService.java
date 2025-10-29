package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.repository.TripRepository;
import com.ridehub.route.service.criteria.SeatLockCriteria;
import com.ridehub.route.service.criteria.TripCriteria;
import com.ridehub.route.service.dto.SeatLockDTO;
import com.ridehub.route.service.dto.TripDTO;
import com.ridehub.route.service.mapper.TripMapper;
import com.ridehub.route.service.vm.TripDetailVM;
import com.ridehub.route.service.vm.VehicleDetailVM;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
 * Service for executing complex queries for {@link Trip} entities in the
 * database.
 * The main input is a {@link TripCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TripDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TripQueryService extends QueryService<Trip> {

        private static final Logger LOG = LoggerFactory.getLogger(TripQueryService.class);

        private final TripRepository tripRepository;

        private final TripMapper tripMapper;
        private final VehicleQueryService vehicleQueryService;
        private final SeatLockQueryService seatLockQueryService;

        public TripQueryService(TripRepository tripRepository, TripMapper tripMapper,
                        VehicleQueryService vehicleQueryService, SeatLockQueryService seatLockQueryService) {
                this.tripRepository = tripRepository;
                this.tripMapper = tripMapper;
                this.vehicleQueryService = vehicleQueryService;
                this.seatLockQueryService = seatLockQueryService;
        }

        /**
         * Return a {@link Page} of {@link TripDTO} which matches the criteria from the
         * database.
         * 
         * @param criteria The object which holds all the filters, which the entities
         *                 should match.
         * @param page     The page, which should be returned.
         * @return the matching entities.
         */
        @Transactional(readOnly = true)
        public Page<TripDTO> findByCriteria(TripCriteria criteria, Pageable page) {
                LOG.debug("find by criteria : {}, page: {}", criteria, page);
                final Specification<Trip> specification = createSpecification(criteria);
                return tripRepository.findAll(specification, page).map(tripMapper::toDto);
        }

        /**
         * Return the number of matching entities in the database.
         * 
         * @param criteria The object which holds all the filters, which the entities
         *                 should match.
         * @return the number of matching entities.
         */
        @Transactional(readOnly = true)
        public long countByCriteria(TripCriteria criteria) {
                LOG.debug("count by criteria : {}", criteria);
                final Specification<Trip> specification = createSpecification(criteria);
                return tripRepository.count(specification);
        }

        @Transactional(readOnly = true)
        public Optional<TripDetailVM> findTripDetail(Long tripId) {
                // 1) Get TripDTO via service (no repo; ensure your TripMapper maps full nested
                // DTOs)
                Trip trip = tripRepository.findById(tripId)
                                .orElseThrow(() -> new EntityNotFoundException("Trip " + tripId));

                // 2️⃣ Convert entity -> DTO
                TripDTO tripDTO = tripMapper.toDto(trip);
                // 2) Vehicle detail (null-safe)
                VehicleDetailVM vehicleDetail = new VehicleDetailVM(null, List.of(), Map.of());
                if (trip.getVehicle() != null && trip.getVehicle().getId() != null) {
                        vehicleDetail = vehicleQueryService
                                        .findDetail(trip.getVehicle().getId())
                                        .orElse(new VehicleDetailVM(tripDTO.getVehicle(), List.of(), Map.of()));
                }

                // 3) Trip seats via QueryService (no repo)
                SeatLockCriteria criteria = new SeatLockCriteria();
                LongFilter tripIdFilter = new LongFilter();
                tripIdFilter.setEquals(tripId);
                criteria.setTripId(tripIdFilter);

                List<SeatLockDTO> tripSeats = seatLockQueryService.findByCriteria(criteria);

                // 4) Compose VM
                return Optional.of(new TripDetailVM(tripDTO, vehicleDetail, tripSeats));
        }

        /**
         * Function to convert {@link TripCriteria} to a {@link Specification}
         * 
         * @param criteria The object which holds all the filters, which the entities
         *                 should match.
         * @return the matching {@link Specification} of the entity.
         */
        protected Specification<Trip> createSpecification(TripCriteria criteria) {
                Specification<Trip> specification = Specification.where(null);
                if (criteria != null) {
                        // This has to be called first, because the distinct method returns null
                        specification = Specification.allOf(
                                        Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct())
                                                        : null,
                                        buildRangeSpecification(criteria.getId(), Trip_.id),
                                        buildStringSpecification(criteria.getTripCode(), Trip_.tripCode),
                                        buildRangeSpecification(criteria.getOccasionFactor(), Trip_.occasionFactor),
                                        buildRangeSpecification(criteria.getDepartureTime(), Trip_.departureTime),
                                        buildRangeSpecification(criteria.getArrivalTime(), Trip_.arrivalTime),
                                        buildRangeSpecification(criteria.getCreatedAt(), Trip_.createdAt),
                                        buildRangeSpecification(criteria.getUpdatedAt(), Trip_.updatedAt),
                                        buildSpecification(criteria.getIsDeleted(), Trip_.isDeleted),
                                        buildRangeSpecification(criteria.getDeletedAt(), Trip_.deletedAt),
                                        buildSpecification(criteria.getDeletedBy(), Trip_.deletedBy),
                                        buildSpecification(criteria.getRouteId(),
                                                        root -> root.join(Trip_.route, JoinType.LEFT).get(Route_.id)),
                                        buildSpecification(criteria.getVehicleId(),
                                                        root -> root.join(Trip_.vehicle, JoinType.LEFT)
                                                                        .get(Vehicle_.id)),
                                        buildSpecification(criteria.getDriverId(),
                                                        root -> root.join(Trip_.driver, JoinType.LEFT).get(Driver_.id)),
                                        buildSpecification(criteria.getAttendantId(),
                                                        root -> root.join(Trip_.attendant, JoinType.LEFT)
                                                                        .get(Attendant_.id)));

                        if (criteria.getOriginDistrictCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getOriginDistrictCode(),
                                                                root -> root.join(Trip_.route, JoinType.LEFT)
                                                                                .join(Route_.origin, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .get(District_.districtCode)));
                        }

                        if (criteria.getOriginProvinceCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getOriginProvinceCode(),
                                                                root -> root.join(Trip_.route, JoinType.LEFT)
                                                                                .join(Route_.origin, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .join(District_.province, JoinType.LEFT)
                                                                                .get(Province_.provinceCode)));
                        }

                        // === NEW: Filter by destination district / province ===
                        if (criteria.getDestinationDistrictCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getDestinationDistrictCode(),
                                                                root -> root.join(Trip_.route, JoinType.LEFT)
                                                                                .join(Route_.destination, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .get(District_.districtCode)));
                        }

                        if (criteria.getDestinationProvinceCode() != null) {
                                specification = specification.and(
                                                buildSpecification(criteria.getDestinationProvinceCode(),
                                                                root -> root.join(Trip_.route, JoinType.LEFT)
                                                                                .join(Route_.destination, JoinType.LEFT)
                                                                                .join(Station_.address, JoinType.LEFT)
                                                                                .join(Address_.ward, JoinType.LEFT)
                                                                                .join(Ward_.district, JoinType.LEFT)
                                                                                .join(District_.province, JoinType.LEFT)
                                                                                .get(Province_.provinceCode)));
                        }
                }
                return specification;
        }

}
