package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.repository.FloorRepository;
import com.ridehub.route.repository.SeatRepository;
import com.ridehub.route.repository.VehicleRepository;
import com.ridehub.route.service.criteria.VehicleCriteria;
import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.dto.SeatDTO;
import com.ridehub.route.service.dto.VehicleDTO;
import com.ridehub.route.service.mapper.FloorMapper;
import com.ridehub.route.service.mapper.SeatMapper;
import com.ridehub.route.service.mapper.VehicleMapper;
import com.ridehub.route.service.vm.VehicleDetailVM;
import com.ridehub.route.service.vm.VehicleWithSeatCountVM;

import jakarta.persistence.criteria.JoinType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Vehicle} entities in the
 * database.
 * The main input is a {@link VehicleCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VehicleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VehicleQueryService extends QueryService<Vehicle> {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleQueryService.class);

    private final VehicleRepository vehicleRepository;

    private final VehicleMapper vehicleMapper;
    private final SeatRepository seatRepository;

    private final FloorRepository floorRepository;
    private final FloorMapper floorMapper;
    private final SeatMapper seatMapper;
    private final SeatQueryService seatQueryService;
    private final FloorQueryService floorQueryService;

    public VehicleQueryService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper,
            SeatRepository seatRepository, FloorMapper floorMapper, FloorRepository floorRepository,
            SeatMapper seatMapper, SeatQueryService seatQueryService, FloorQueryService floorQueryService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.seatRepository = seatRepository;
        this.floorRepository = floorRepository;
        this.floorMapper = floorMapper;
        this.seatMapper = seatMapper;
        this.seatQueryService = seatQueryService;
        this.floorQueryService = floorQueryService;
    }

    /**
     * Return a {@link Page} of {@link VehicleDTO} which matches the criteria from
     * the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VehicleDTO> findByCriteria(VehicleCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Vehicle> specification = createSpecification(criteria);
        return vehicleRepository.findAll(specification, page).map(vehicleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VehicleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Vehicle> specification = createSpecification(criteria);
        return vehicleRepository.count(specification);
    }

    @Transactional(readOnly = true)
    public Page<VehicleWithSeatCountVM> findByCriteriaWithSeatCount(VehicleCriteria criteria, Pageable pageable) {
        Page<VehicleDTO> vehiclePage = findByCriteria(criteria, pageable);
        List<VehicleDTO> vehicleDTOs = vehiclePage.getContent();

        if (vehicleDTOs.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> vehicleIds = vehicleDTOs.stream()
                .map(VehicleDTO::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, Long> seatCounts = seatQueryService.countSeatsByVehicleIds(vehicleIds);

        List<VehicleWithSeatCountVM> wrapped = vehicleDTOs.stream()
                .map(v -> new VehicleWithSeatCountVM(seatCounts.getOrDefault(v.getId(), 0L), v))
                .toList();

        return new PageImpl<>(wrapped, pageable, vehiclePage.getTotalElements());
    }

    /**
     * Find all vehicles using direct repository access.
     * This is more efficient than using empty criteria.
     * 
     * @return list of all vehicles
     */
    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        LOG.debug("find all vehicles");
        return vehicleRepository.findAll();
    }

    /**
     * Function to convert {@link VehicleCriteria} to a {@link Specification}
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Vehicle> createSpecification(VehicleCriteria criteria) {
        Specification<Vehicle> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                    buildRangeSpecification(criteria.getId(), Vehicle_.id),
                    buildSpecification(criteria.getType(), Vehicle_.type),
                    buildRangeSpecification(criteria.getTypeFactor(), Vehicle_.typeFactor),
                    buildStringSpecification(criteria.getPlateNumber(), Vehicle_.plateNumber),
                    buildStringSpecification(criteria.getBrand(), Vehicle_.description),
                    buildSpecification(criteria.getStatus(), Vehicle_.status),
                    buildRangeSpecification(criteria.getCreatedAt(), Vehicle_.createdAt),
                    buildRangeSpecification(criteria.getUpdatedAt(), Vehicle_.updatedAt),
                    buildSpecification(criteria.getIsDeleted(), Vehicle_.isDeleted),
                    buildRangeSpecification(criteria.getDeletedAt(), Vehicle_.deletedAt),
                    buildSpecification(criteria.getDeletedBy(), Vehicle_.deletedBy),
                    buildSpecification(criteria.getSeatMapId(),
                            root -> root.join(Vehicle_.seatMap, JoinType.LEFT).get(SeatMap_.id)),
                    buildSpecification(criteria.getVehicleImgId(),
                            root -> root.join(Vehicle_.vehicleImg, JoinType.LEFT).get(FileRoute_.id)));
        }
        return specification;
    }

    @Transactional(readOnly = true)
    public Optional<VehicleDetailVM> findDetail(Long id) {
        return vehicleRepository.findById(id).map(vehicle -> {
            VehicleDTO vehicleDTO = vehicleMapper.toDto(vehicle);

            SeatMap seatMap = vehicle.getSeatMap();
            if (seatMap == null || seatMap.getId() == null) {
                return new VehicleDetailVM(vehicleDTO, List.of(), Map.of());
            }

            // ✅ Use FloorQueryService instead of repository
            List<FloorDTO> floorDTOs = floorQueryService.findFloorsBySeatMapId(seatMap.getId());

            // seats grouped by floorId
            Map<Long, List<SeatDTO>> seatsByFloorId = Map.of();
            if (!floorDTOs.isEmpty()) {
                List<Long> floorIds = floorDTOs.stream()
                        .map(FloorDTO::getId)
                        .filter(Objects::nonNull)
                        .toList();

                if (!floorIds.isEmpty()) {
                    // Inline Criteria query for seats
                    List<SeatDTO> seatDTOs = seatQueryService.findByFloorIds(floorIds);

                    seatsByFloorId = seatDTOs.stream()
                            .filter(s -> s.getFloor() != null && s.getFloor().getId() != null)
                            .collect(Collectors.groupingBy(
                                    s -> s.getFloor().getId(),
                                    Collectors.toList()));
                }
            }

            return new VehicleDetailVM(vehicleDTO, floorDTOs, seatsByFloorId);
        });
    }
}
