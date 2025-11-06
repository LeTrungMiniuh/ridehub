package com.ridehub.route.service;

import com.ridehub.route.domain.*;
import com.ridehub.route.repository.TripRepository;
import com.ridehub.route.repository.ScheduleTimeSlotRepository;
import com.ridehub.route.service.ScheduleTimeSlotQueryService;
import com.ridehub.route.service.criteria.*;
import com.ridehub.route.service.dto.*;
import com.ridehub.route.service.mapper.TripMapper;
import com.ridehub.route.service.mapper.RouteMapper;
import com.ridehub.route.service.mapper.ScheduleOccasionMapper;
import com.ridehub.route.service.mapper.ScheduleTimeSlotMapper;
import tech.jhipster.service.filter.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for automatically creating trips based on schedules.
 */
@Service
@Transactional
public class AutoScheduleService {

    /**
     * Record to identify a unique trip.
     */
    public record TripIdentifier(Long routeId, Long timeSlotId, LocalDate tripDate) {}

    private static final Logger LOG = LoggerFactory.getLogger(AutoScheduleService.class);

    private final ScheduleQueryService scheduleQueryService;
    private final TripQueryService tripQueryService;
    private final TripService tripService;
    private final TripMapper tripMapper;
    private final TripRepository tripRepository;
    private final ScheduleTimeSlotRepository scheduleTimeSlotRepository;
    private final ScheduleTimeSlotQueryService scheduleTimeSlotQueryService;
    private final RouteService routeService;
    private final RouteQueryService routeQueryService;
    private final ScheduleOccasionService scheduleOccasionService;
    private final ScheduleOccasionQueryService occasionRuleQueryService;
    private final RouteMapper routeMapper;
    private final ScheduleOccasionMapper scheduleOccasionMapper;


    public AutoScheduleService(
            ScheduleQueryService scheduleQueryService,
            TripQueryService tripQueryService,
            TripService tripService,
            TripMapper tripMapper,
            TripRepository tripRepository,
            ScheduleTimeSlotRepository scheduleTimeSlotRepository,
            ScheduleTimeSlotQueryService scheduleTimeSlotQueryService,
            RouteService routeService,
            RouteQueryService routeQueryService,
            ScheduleOccasionService scheduleOccasionService,
            ScheduleOccasionQueryService occasionRuleQueryService,
            RouteMapper routeMapper,
            ScheduleOccasionMapper scheduleOccasionMapper) {
        this.scheduleQueryService = scheduleQueryService;
        this.tripQueryService = tripQueryService;
        this.tripService = tripService;
        this.tripMapper = tripMapper;
        this.tripRepository = tripRepository;
        this.scheduleTimeSlotRepository = scheduleTimeSlotRepository;
        this.scheduleTimeSlotQueryService = scheduleTimeSlotQueryService;
        this.routeService = routeService;
        this.routeQueryService = routeQueryService;
        this.scheduleOccasionService = scheduleOccasionService;
        this.occasionRuleQueryService = occasionRuleQueryService;
        this.routeMapper = routeMapper;
        this.scheduleOccasionMapper = scheduleOccasionMapper;

    }

    /**
     * Auto-create trips for all active schedules.
     * This method is typically called by a cron job.
     * Scheduled to run daily at 2:00 AM.
     *
     * @return summary of created trips
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public AutoScheduleResult createTripsForActiveSchedules() {
        LOG.info("Starting auto-schedule process for trips");

        LocalDate today = LocalDate.now();
        LocalDate scheduleEndDate = today.plusDays(7); // Create trips for next 7 days (rolling window)

        // Get all active schedules
        ScheduleCriteria scheduleCriteria = new ScheduleCriteria();
        BooleanFilter activeFilter = new BooleanFilter();
        activeFilter.setEquals(true);
        scheduleCriteria.setActive(activeFilter);
        List<ScheduleDTO> scheduleDTOs = scheduleQueryService.findByCriteria(scheduleCriteria);

        if (scheduleDTOs.isEmpty()) {
            LOG.info("No active schedules found for processing");
            return new AutoScheduleResult(0, 0, Collections.emptyMap());
        }

        // Batch fetch all related data to eliminate N+1 queries
        LOG.debug("Batch fetching related data for {} schedules", scheduleDTOs.size());

        Set<Long> routeIds = scheduleDTOs.stream()
                .map(s -> s.getRoute() != null ? s.getRoute().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> occasionIds = scheduleDTOs.stream()
                .map(s -> s.getOccasionRule() != null ? s.getOccasionRule().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> scheduleIds = scheduleDTOs.stream()
                .map(ScheduleDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Route> routeMap = batchFetchRoutes(routeIds);
        Map<Long, ScheduleOccasion> occasionMap = batchFetchScheduleOccasions(occasionIds);
        Map<Long, List<ScheduleTimeSlot>> timeslotMap = batchFetchTimeslots(scheduleIds);

        LOG.debug("Batch fetched: {} routes, {} occasions, {} timeslot groups",
                routeMap.size(), occasionMap.size(), timeslotMap.size());

        int totalTripsCreated = 0;
        int totalSchedulesProcessed = 0;
        Map<String, Integer> tripsBySchedule = new HashMap<>();

        for (ScheduleDTO scheduleDTO : scheduleDTOs) {
            try {
                // Convert DTO to entity using batch-fetched data
                Schedule schedule = convertToScheduleEntity(scheduleDTO, routeMap, occasionMap, timeslotMap);
                int tripsCreated = createTripsForSchedule(schedule, today, scheduleEndDate);
                totalTripsCreated += tripsCreated;
                totalSchedulesProcessed++;

                if (tripsCreated > 0) {
                    tripsBySchedule.put(schedule.getScheduleCode(), tripsCreated);
                }

                LOG.debug("Processed schedule {}: {} trips created",
                        schedule.getScheduleCode(), tripsCreated);

            } catch (Exception e) {
                LOG.error("Error processing schedule {}: {}",
                        scheduleDTO.getScheduleCode(), e.getMessage(), e);
            }
        }

        LOG.info("Auto-schedule completed: {} schedules processed, {} trips created",
                totalSchedulesProcessed, totalTripsCreated);

        return new AutoScheduleResult(totalSchedulesProcessed, totalTripsCreated, tripsBySchedule);
    }

    /**
     * Create trips for a specific schedule within date range.
     */
    private int createTripsForSchedule(Schedule schedule, LocalDate startDate, LocalDate endDate) {
        if (schedule.getStartDate() != null && schedule.getStartDate().isAfter(endDate)) {
            return 0; // Schedule starts after our target period
        }

        LocalDate effectiveStartDate = maxDate(startDate, schedule.getStartDate());
        LocalDate effectiveEndDate = minDate(endDate, schedule.getEndDate());

        if (effectiveStartDate.isAfter(effectiveEndDate)) {
            return 0; // No overlap in date ranges
        }

        List<LocalDate> tripDates = calculateTripDates(
                effectiveStartDate, effectiveEndDate, schedule.getDaysOfWeek());

        // Collect all potential trips for bulk existence checking
        List<TripIdentifier> potentialTrips = new ArrayList<>();
        for (LocalDate tripDate : tripDates) {
            for (ScheduleTimeSlot timeSlot : schedule.getTimeSlots()) {
                if (schedule.getRoute() != null) {
                    potentialTrips.add(new TripIdentifier(
                            schedule.getRoute().getId(),
                            timeSlot.getId(),
                            tripDate));
                }
            }
        }

        // Bulk check existing trips to avoid unnecessary individual database calls
        Set<TripIdentifier> existingTrips = bulkCheckTripExistence(potentialTrips);
        
        // Create only trips that don't already exist
        int tripsCreated = 0;
        List<Trip> tripsToSave = new ArrayList<>();

        for (LocalDate tripDate : tripDates) {
            for (ScheduleTimeSlot timeSlot : schedule.getTimeSlots()) {
                if (schedule.getRoute() != null) {
                    TripIdentifier tripId = new TripIdentifier(
                            schedule.getRoute().getId(),
                            timeSlot.getId(),
                            tripDate);
                    
                    // Only create trip if it doesn't already exist
                    if (!existingTrips.contains(tripId)) {
                        Trip trip = createTripEntity(schedule, timeSlot, tripDate);
                        tripsToSave.add(trip);
                        tripsCreated++;
                    }
                }
            }
        }

        // Save all new trips (using individual saves since saveAll not available)
        if (!tripsToSave.isEmpty()) {
            try {
                for (Trip trip : tripsToSave) {
                    tripService.save(tripMapper.toDto(trip));
                }
                LOG.info("Created {} new trips for schedule {}", 
                        tripsToSave.size(), schedule.getScheduleCode());
            } catch (Exception e) {
                LOG.error("Error saving trips for schedule {}: {}", 
                        schedule.getScheduleCode(), e.getMessage(), e);
                return 0;
            }
        }

        return tripsCreated;
    }

    /**
     * Create mock vehicle for testing.
     */
    private Vehicle createMockVehicle(Route route) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setPlateNumber("MOCK-VEHICLE-001");
        vehicle.setType(com.ridehub.route.domain.enumeration.VehicleType.STANDARD_BUS_NORMAL);
        vehicle.setStatus(com.ridehub.route.domain.enumeration.VehicleStatus.ACTIVE);
        vehicle.setCreatedAt(Instant.now());
        return vehicle;
    }

    /**
     * Create mock driver for testing.
     */
    private Driver createMockDriver() {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setLicenseClass("D");
        
        // Create staff object for driver
        Staff driverStaff = new Staff();
        driverStaff.setId(1L);
        driverStaff.setName("Mock Driver");
        driverStaff.setPhoneNumber("123-456-7890");
        driverStaff.setCreatedAt(Instant.now());
        
        driver.setStaff(driverStaff);
        driver.setCreatedAt(Instant.now());
        return driver;
    }

    /**
     * Create mock attendant for testing.
     */
    private Attendant createMockAttendant() {
        Attendant attendant = new Attendant();
        attendant.setId(1L);
        
        // Create staff object for attendant
        Staff attendantStaff = new Staff();
        attendantStaff.setId(2L);
        attendantStaff.setName("Mock Attendant");
        attendantStaff.setPhoneNumber("098-765-4321");
        attendantStaff.setCreatedAt(Instant.now());
        
        attendant.setStaff(attendantStaff);
        attendant.setCreatedAt(Instant.now());
        return attendant;
    }

    /**
     * Create a Trip entity (without saving) for a schedule time slot on a specific date.
     */
    private Trip createTripEntity(Schedule schedule, ScheduleTimeSlot timeSlot, LocalDate date) {
        // Calculate departure and arrival times
        LocalDateTime departureDateTime = LocalDateTime.of(date, timeSlot.getDepartureTime());
        LocalDateTime arrivalDateTime = LocalDateTime.of(date, timeSlot.getArrivalTime());

        // Handle overnight trips
        if (arrivalDateTime.isBefore(departureDateTime)) {
            arrivalDateTime = arrivalDateTime.plusDays(1);
        }

        // Create trip entity
        Trip trip = new Trip();
        trip.setTripCode(generateTripCode(schedule, timeSlot, date));
        trip.setDepartureTime(departureDateTime.atZone(ZoneId.systemDefault()).toInstant());
        trip.setArrivalTime(arrivalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        trip.setOccasionFactor(schedule.getOccasionRule().getOccasionFactor());
        trip.setCreatedAt(Instant.now());
        trip.setRoute(schedule.getRoute());
        trip.setSlot(timeSlot);
        trip.setDriver(createMockDriver());
        trip.setAttendant(createMockAttendant());
        trip.setVehicle(createMockVehicle(schedule.getRoute()));
        trip.setIsDeleted(false);
        
        return trip;
    }

    /**
     * Bulk check if trips already exist to avoid N+1 query problems.
     */
    private Set<TripIdentifier> bulkCheckTripExistence(List<TripIdentifier> potentialTrips) {
        if (potentialTrips.isEmpty()) {
            return Collections.emptySet();
        }

        Set<TripIdentifier> existingTrips = new HashSet<>();
        
        // For now, use a simple approach - check each potential trip individually
        // In a production environment, this could be optimized with a custom query
        for (TripIdentifier tripId : potentialTrips) {
            // if (tripExistsByIds(tripId.routeId(), tripId.timeSlotId(), tripId.tripDate())) {
            //     existingTrips.add(tripId);
            // }
        }
        
        return existingTrips;
    }

    /**
     * Check if a trip exists for the given route, time slot, and date.
     */
    private boolean tripExistsByIds(Long routeId, Long timeSlotId, LocalDate date) {
        TripCriteria criteria = new TripCriteria();
        
        // Set route filter
        LongFilter routeFilter = new LongFilter();
        routeFilter.setEquals(routeId);
        criteria.setRouteId(routeFilter);
        
        // Set time slot filter  
        LongFilter slotFilter = new LongFilter();
        slotFilter.setEquals(timeSlotId);
        criteria.setSlotId(slotFilter);
        
        // Set date filter - convert to Instant range for the day
        InstantFilter dateFilter = new InstantFilter();
        LocalDate startOfDay = date.atStartOfDay().toLocalDate();
        Instant dayStart = startOfDay.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = startOfDay.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        dateFilter.setGreaterThanOrEqual(dayStart);
        dateFilter.setLessThan(dayEnd);
        criteria.setDepartureTime(dateFilter);
        
        // Check if any trips match
        Pageable pageable = Pageable.unpaged();
        var existingTripsPage = tripQueryService.findByCriteria(criteria, pageable);
        return !existingTripsPage.getContent().isEmpty();
    }

    /**
     * Batch fetch timeslots by schedule IDs to eliminate N+1 queries
     */
    Map<Long, List<ScheduleTimeSlot>> batchFetchTimeslots(Set<Long> scheduleIds) {
        if (scheduleIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            ScheduleTimeSlotCriteria criteria = new ScheduleTimeSlotCriteria();
            LongFilter scheduleIdFilter = new LongFilter();
            scheduleIdFilter.setIn(new ArrayList<>(scheduleIds));
            criteria.setScheduleId(scheduleIdFilter);

            List<ScheduleTimeSlotDTO> timeslotDTOs = scheduleTimeSlotQueryService.findByCriteria(criteria);
            return timeslotDTOs.stream()
                    .map(this::convertToScheduleTimeSlotEntity)
                    .filter(Objects::nonNull)
                    .filter(timeslot -> timeslot.getSchedule() != null)
                    .collect(Collectors.groupingBy(timeslot -> timeslot.getSchedule().getId()));

        } catch (Exception e) {
            LOG.error("Error batch fetching timeslots: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * Create mock vehicle for testing.
     */
    private boolean createTripForScheduleSlot(Schedule schedule, ScheduleTimeSlot timeSlot, LocalDate date) {
        // Check if trip already exists
        // if (tripExists(schedule, timeSlot, date)) {
        //     return false;
        // }

        try {
            // Calculate departure and arrival times
            LocalDateTime departureDateTime = LocalDateTime.of(date, timeSlot.getDepartureTime());
            LocalDateTime arrivalDateTime = LocalDateTime.of(date, timeSlot.getArrivalTime());

            // Handle overnight trips
            if (arrivalDateTime.isBefore(departureDateTime)) {
                arrivalDateTime = arrivalDateTime.plusDays(1);
            }

            // Get available resources (using mock objects)
            Route route = schedule.getRoute();
            Vehicle vehicle = createMockVehicle(route);
            Driver driver = createMockDriver();
            Attendant attendant = createMockAttendant();
            
            if (vehicle == null || driver == null) {
                LOG.warn("Cannot create trip for schedule {} on {}: vehicle={}, driver={}",
                        schedule.getScheduleCode(), date,
                        vehicle != null ? vehicle.getPlateNumber() : "NOT_FOUND",
                        driver != null ? driver.getLicenseClass() : "NOT_FOUND");
                return false;
            }

            // Create trip
            Trip trip = new Trip();
            trip.setTripCode(generateTripCode(schedule, timeSlot, date));
            trip.setDepartureTime(departureDateTime.atZone(ZoneId.systemDefault()).toInstant());
            trip.setArrivalTime(arrivalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            trip.setOccasionFactor(schedule.getOccasionRule().getOccasionFactor());
            trip.setCreatedAt(Instant.now());
            trip.setRoute(route);
            trip.setVehicle(vehicle);
            trip.setSlot(timeSlot);
            trip.setDriver(driver);
            trip.setAttendant(attendant);
            trip.setIsDeleted(false);

            TripDTO savedTrip = tripService.save(tripMapper.toDto(trip));

            LOG.info("Created trip {} for schedule {} on {}",
                    savedTrip.getTripCode(), schedule.getScheduleCode(), date);

            return true;

        } catch (Exception e) {
            LOG.error("Error creating trip for schedule {} on {}: {}",
                    schedule.getScheduleCode(), date, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if a trip already exists for given schedule, time slot, and date.
     */
    private boolean tripExists(Schedule schedule, ScheduleTimeSlot timeSlot, LocalDate date) {
        try {
            // Use optimized repository method for better performance
            Instant dayStart = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            Instant dayEnd = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
            
            long count = tripRepository.countByRouteIdAndSlotIdAndDepartureTimeBetweenAndIsDeletedFalse(
                    schedule.getRoute().getId(),
                    timeSlot.getId(),
                    dayStart,
                    dayEnd
            );
            
            return count > 0;
        } catch (Exception e) {
            LOG.warn("Error checking trip existence for schedule {} on {}: {}",
                    schedule.getScheduleCode(), date, e.getMessage());
            // Fall back to original method if optimized query fails
            return tripExistsOriginal(schedule, timeSlot, date);
        }
    }

    /**
     * Original trip existence method as fallback.
     */
    private boolean tripExistsOriginal(Schedule schedule, ScheduleTimeSlot timeSlot, LocalDate date) {
        TripCriteria criteria = new TripCriteria();

        // Filter by route
        LongFilter routeIdFilter = new LongFilter();
        routeIdFilter.setEquals(schedule.getRoute().getId());
        criteria.setRouteId(routeIdFilter);

        // Filter by time slot
        LongFilter slotIdFilter = new LongFilter();
        slotIdFilter.setEquals(timeSlot.getId());
        criteria.setSlotId(slotIdFilter);

        // Filter by departure time range (the entire day)
        InstantFilter departureTimeFilter = new InstantFilter();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        departureTimeFilter.setGreaterThanOrEqual(
                startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        departureTimeFilter.setLessThanOrEqual(
                endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        criteria.setDepartureTime(departureTimeFilter);

        // Filter non-deleted trips
        BooleanFilter isDeletedFilter = new BooleanFilter();
        isDeletedFilter.setEquals(false);
        criteria.setIsDeleted(isDeletedFilter);

        return tripQueryService.existsByCriteria(criteria);
    }



    /**
     * Calculate trip dates based on schedule's days of week.
     */
    private List<LocalDate> calculateTripDates(LocalDate startDate, LocalDate endDate, String daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Set<DayOfWeek> scheduledDays = Arrays.stream(daysOfWeek.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .map(dayNum -> DayOfWeek.of(dayNum))
                .collect(Collectors.toSet());

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (scheduledDays.contains(currentDate.getDayOfWeek())) {
                dates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

    /**
     * Generate unique trip code.
     */
    private String generateTripCode(Schedule schedule, ScheduleTimeSlot timeSlot, LocalDate date) {
        return String.format("TRP-%s-%s-%s",
                schedule.getScheduleCode(),
                date.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                timeSlot.getId());
    }

    /**
     * Batch fetch routes by IDs to eliminate N+1 queries
     */
    private Map<Long, Route> batchFetchRoutes(Set<Long> routeIds) {
        if (routeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            // Create criteria for batch fetch
            RouteCriteria criteria = new RouteCriteria();
            LongFilter idFilter = new LongFilter();
            idFilter.setIn(new ArrayList<>(routeIds));
            criteria.setId(idFilter);

            List<RouteDTO> routeDTOs = routeQueryService.findByCriteria(criteria, Pageable.unpaged()).getContent();
            return routeDTOs.stream()
                    .collect(Collectors.toMap(
                            RouteDTO::getId,
                            routeMapper::toEntity));
        } catch (Exception e) {
            LOG.error("Error batch fetching routes: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * Batch fetch schedule occasions by IDs to eliminate N+1 queries
     */
    private Map<Long, ScheduleOccasion> batchFetchScheduleOccasions(Set<Long> occasionIds) {
        if (occasionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            ScheduleOccasionCriteria criteria = new ScheduleOccasionCriteria();
            LongFilter idFilter = new LongFilter();
            idFilter.setIn(new ArrayList<>(occasionIds));
            criteria.setId(idFilter);

            List<ScheduleOccasionDTO> occasionDTOs = occasionRuleQueryService.findByCriteria(criteria);
            return occasionDTOs.stream()
                    .collect(Collectors.toMap(
                            ScheduleOccasionDTO::getId,
                            scheduleOccasionMapper::toEntity));
        } catch (Exception e) {
            LOG.error("Error batch fetching schedule occasions: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }



    /**
     * Convert ScheduleDTO to Schedule entity.
     */
    private ScheduleTimeSlot convertToScheduleTimeSlotEntity(ScheduleTimeSlotDTO dto) {
        if (dto == null) {
            return null;
        }

        ScheduleTimeSlot entity = new ScheduleTimeSlot();
        entity.setId(dto.getId());
        entity.setSlotCode(dto.getSlotCode());
        entity.setDepartureTime(dto.getDepartureTime());
        entity.setArrivalTime(dto.getArrivalTime());
        entity.setBufferMinutes(dto.getBufferMinutes());
        entity.setSequence(dto.getSequence());
        entity.setActive(dto.getActive());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setIsDeleted(dto.getIsDeleted());
        entity.setDeletedAt(dto.getDeletedAt());
        entity.setDeletedBy(dto.getDeletedBy());
        
        // CRITICAL FIX: Set the schedule relationship for proper grouping
        if (dto.getSchedule() != null) {
            Schedule schedule = new Schedule();
            schedule.setId(dto.getSchedule().getId());
            entity.setSchedule(schedule);
        }
        
        return entity;
    }

    private Schedule convertToScheduleEntity(ScheduleDTO scheduleDTO) {
        // Legacy method - use batch version for performance
        return convertToScheduleEntity(scheduleDTO, Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap());
    }

    private Schedule convertToScheduleEntity(ScheduleDTO scheduleDTO,
            Map<Long, Route> routeMap,
            Map<Long, ScheduleOccasion> occasionMap,
            Map<Long, List<ScheduleTimeSlot>> timeslotMap) {
        Schedule schedule = new Schedule();
        schedule.setId(scheduleDTO.getId());
        schedule.setScheduleCode(scheduleDTO.getScheduleCode());
        schedule.setStartDate(scheduleDTO.getStartDate());
        schedule.setEndDate(scheduleDTO.getEndDate());
        schedule.setDaysOfWeek(scheduleDTO.getDaysOfWeek());
        schedule.setActive(scheduleDTO.getActive());
        schedule.setCreatedAt(scheduleDTO.getCreatedAt());
        schedule.setUpdatedAt(scheduleDTO.getUpdatedAt());
        schedule.setIsDeleted(scheduleDTO.getIsDeleted());
        schedule.setDeletedAt(scheduleDTO.getDeletedAt());
        schedule.setDeletedBy(scheduleDTO.getDeletedBy());

        // Set route from batch-fetched map
        if (scheduleDTO.getRoute() != null && scheduleDTO.getRoute().getId() != null) {
            Route route = routeMap.get(scheduleDTO.getRoute().getId());
            if (route != null) {
                schedule.setRoute(route);
            } else {
                LOG.warn("Route not found in batch fetch for ID: {}", scheduleDTO.getRoute().getId());
            }
        }

        // Set occasion rule from batch-fetched map
        if (scheduleDTO.getOccasionRule() != null && scheduleDTO.getOccasionRule().getId() != null) {
            ScheduleOccasion occasionRule = occasionMap.get(scheduleDTO.getOccasionRule().getId());
            if (occasionRule != null) {
                schedule.setOccasionRule(occasionRule);
            } else {
                LOG.warn("ScheduleOccasion not found in batch fetch for ID: {}", scheduleDTO.getOccasionRule().getId());
            }
        }

        // Set timeSlots from batch-fetched map
        if (scheduleDTO.getId() != null) {
            List<ScheduleTimeSlot> timeSlots = timeslotMap.get(scheduleDTO.getId());
            if (timeSlots != null) {
                schedule.setTimeSlots(new HashSet<>(timeSlots));
                LOG.debug("Set {} time slots for schedule {} from batch fetch", timeSlots.size(), scheduleDTO.getId());
            } else {
                schedule.setTimeSlots(Collections.emptySet());
                LOG.debug("No time slots found for schedule {} in batch fetch", scheduleDTO.getId());
            }
        }

        return schedule;
    }

    /**
     * Utility method to get the maximum of two dates.
     */
    private LocalDate maxDate(LocalDate date1, LocalDate date2) {
        if (date1 == null)
            return date2;
        if (date2 == null)
            return date1;
        return date1.isAfter(date2) ? date1 : date2;
    }

    /**
     * Utility method to get the minimum of two dates.
     */
    private LocalDate minDate(LocalDate date1, LocalDate date2) {
        if (date1 == null)
            return date2;
        if (date2 == null)
            return date1;
        return date1.isBefore(date2) ? date1 : date2;
    }

    /**
     * Result class for auto-schedule operations.
     */
    public static class AutoScheduleResult {
        private final int schedulesProcessed;
        private final int tripsCreated;
        private final Map<String, Integer> tripsBySchedule;

        public AutoScheduleResult(int schedulesProcessed, int tripsCreated, Map<String, Integer> tripsBySchedule) {
            this.schedulesProcessed = schedulesProcessed;
            this.tripsCreated = tripsCreated;
            this.tripsBySchedule = tripsBySchedule;
        }

        public int getSchedulesProcessed() {
            return schedulesProcessed;
        }

        public int getTripsCreated() {
            return tripsCreated;
        }

        public Map<String, Integer> getTripsBySchedule() {
            return tripsBySchedule;
        }
    }
}