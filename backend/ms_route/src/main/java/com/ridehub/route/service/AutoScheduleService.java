package com.ridehub.route.service;

import com.ridehub.route.domain.*;
import com.ridehub.route.service.criteria.*;
import com.ridehub.route.service.dto.*;
import com.ridehub.route.service.mapper.TripMapper;
import tech.jhipster.service.filter.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for automatically creating trips based on schedules.
 */
@Service
@Transactional
public class AutoScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(AutoScheduleService.class);

    private final ScheduleQueryService scheduleQueryService;
    private final TripQueryService tripQueryService;
    private final TripService tripService;
    private final TripMapper tripMapper;
    private final VehicleQueryService vehicleQueryService;
    private final DriverQueryService driverQueryService;
    private final AttendantQueryService attendantQueryService;

    public AutoScheduleService(
        ScheduleQueryService scheduleQueryService,
        TripQueryService tripQueryService,
        TripService tripService,
        TripMapper tripMapper,
        VehicleQueryService vehicleQueryService,
        DriverQueryService driverQueryService,
        AttendantQueryService attendantQueryService
    ) {
        this.scheduleQueryService = scheduleQueryService;
        this.tripQueryService = tripQueryService;
        this.tripService = tripService;
        this.tripMapper = tripMapper;
        this.vehicleQueryService = vehicleQueryService;
        this.driverQueryService = driverQueryService;
        this.attendantQueryService = attendantQueryService;
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
        LocalDate scheduleEndDate = today.plusDays(30); // Create trips for next 30 days
        
        // Get all active schedules
        ScheduleCriteria scheduleCriteria = new ScheduleCriteria();
        BooleanFilter activeFilter = new BooleanFilter();
        activeFilter.setEquals(true);
        scheduleCriteria.setActive(activeFilter);
        List<ScheduleDTO> scheduleDTOs = scheduleQueryService.findByCriteria(scheduleCriteria);
        
        int totalTripsCreated = 0;
        int totalSchedulesProcessed = 0;
        Map<String, Integer> tripsBySchedule = new HashMap<>();
        
        for (ScheduleDTO scheduleDTO : scheduleDTOs) {
            try {
                // Convert DTO to entity for processing
                Schedule schedule = convertToScheduleEntity(scheduleDTO);
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
        
        int tripsCreated = 0;
        
        for (LocalDate tripDate : tripDates) {
            for (ScheduleTimeSlot timeSlot : schedule.getTimeSlots()) {
                if (createTripForScheduleSlot(schedule, timeSlot, tripDate)) {
                    tripsCreated++;
                }
            }
        }
        
        return tripsCreated;
    }

    /**
     * Create a single trip for a schedule time slot on a specific date.
     */
    private boolean createTripForScheduleSlot(Schedule schedule, ScheduleTimeSlot timeSlot, LocalDate date) {
        // Check if trip already exists
        if (tripExists(schedule, timeSlot, date)) {
            return false;
        }
        
        try {
            // Calculate departure and arrival times
            LocalDateTime departureDateTime = LocalDateTime.of(date, timeSlot.getDepartureTime());
            LocalDateTime arrivalDateTime = LocalDateTime.of(date, timeSlot.getArrivalTime());
            
            // Handle overnight trips
            if (arrivalDateTime.isBefore(departureDateTime)) {
                arrivalDateTime = arrivalDateTime.plusDays(1);
            }
            
            // Get available resources (simplified approach)
            Route route = schedule.getRoute();
            Vehicle vehicle = findAvailableVehicle(route);
            Driver driver = findAvailableDriver();
            Attendant attendant = findAvailableAttendant();
            
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
     * Find available vehicle for the given route.
     */
    private Vehicle findAvailableVehicle(Route route) {
        // Simple implementation: get first available vehicle
        // In a real implementation, you would check vehicle availability, capacity, etc.
        try {
            // Use query service for simple findAll operation
            List<Vehicle> availableVehicles = vehicleQueryService.findAll();
            return availableVehicles.isEmpty() ? null : availableVehicles.get(0);
        } catch (Exception e) {
            LOG.warn("Error finding available vehicle: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Find available driver for the time period.
     */
    private Driver findAvailableDriver() {
        try {
            // Use query service for simple findAll operation
            List<Driver> availableDrivers = driverQueryService.findEntitiesByCriteria(new DriverCriteria());
            return availableDrivers.isEmpty() ? null : availableDrivers.get(0);
        } catch (Exception e) {
            LOG.warn("Error finding available driver: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Find available attendant for the time period.
     */
    private Attendant findAvailableAttendant() {
        try {
            // Use query service for simple findAll operation
            List<Attendant> availableAttendants = attendantQueryService.findEntitiesByCriteria(new AttendantCriteria());
            return availableAttendants.isEmpty() ? null : availableAttendants.get(0);
        } catch (Exception e) {
            LOG.warn("Error finding available attendant: {}", e.getMessage());
            return null;
        }
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
     * Convert ScheduleDTO to Schedule entity.
     */
    private Schedule convertToScheduleEntity(ScheduleDTO scheduleDTO) {
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
        return schedule;
    }

    /**
     * Utility method to get the maximum of two dates.
     */
    private LocalDate maxDate(LocalDate date1, LocalDate date2) {
        if (date1 == null) return date2;
        if (date2 == null) return date1;
        return date1.isAfter(date2) ? date1 : date2;
    }

    /**
     * Utility method to get the minimum of two dates.
     */
    private LocalDate minDate(LocalDate date1, LocalDate date2) {
        if (date1 == null) return date2;
        if (date2 == null) return date1;
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