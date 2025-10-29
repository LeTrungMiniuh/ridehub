package com.ridehub.route.web.rest;

import com.ridehub.route.service.AutoScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for auto-scheduling trips.
 */
@RestController
@RequestMapping("/api/auto-schedule")
@Tag(name = "Auto Schedule", description = "Auto schedule management")
public class AutoScheduleResource {

    private static final Logger LOG = LoggerFactory.getLogger(AutoScheduleResource.class);

    private final AutoScheduleService autoScheduleService;

    public AutoScheduleResource(AutoScheduleService autoScheduleService) {
        this.autoScheduleService = autoScheduleService;
    }

    /**
     * POST /api/auto-schedule/trigger : Manually trigger auto-scheduling process
     *
     * @return the ResponseEntity with status 200 (OK) and with auto-schedule result
     */
    @PostMapping("/trigger")
    @Operation(summary = "Manually trigger auto-scheduling process", description = "Triggers the auto-scheduling process to create trips for active schedules")
    public ResponseEntity<AutoScheduleResponse> triggerAutoSchedule() {
        LOG.info("REST request to trigger auto-scheduling");
        
        try {
            AutoScheduleService.AutoScheduleResult result = autoScheduleService.createTripsForActiveSchedules();
            
            AutoScheduleResponse response = new AutoScheduleResponse(
                true,
                "Auto-scheduling completed successfully",
                result.getSchedulesProcessed(),
                result.getTripsCreated(),
                result.getTripsBySchedule()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOG.error("Error during auto-scheduling: {}", e.getMessage(), e);
            
            AutoScheduleResponse response = new AutoScheduleResponse(
                false,
                "Auto-scheduling failed: " + e.getMessage(),
                0,
                0,
                null
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/auto-schedule/status : Get auto-scheduling status
     *
     * @return the ResponseEntity with status 200 (OK) and status information
     */
    @GetMapping("/status")
    @Operation(summary = "Get auto-scheduling status", description = "Returns the current status of the auto-scheduling system")
    public ResponseEntity<AutoScheduleStatus> getAutoScheduleStatus() {
        LOG.debug("REST request to get auto-scheduling status");
        
        AutoScheduleStatus status = new AutoScheduleStatus(
            "Auto-scheduling service is running",
            "Cron job is configured to run daily at 2:00 AM",
            System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(status);
    }

    /**
     * Response class for auto-schedule operations.
     */
    public static class AutoScheduleResponse {
        private final boolean success;
        private final String message;
        private final int schedulesProcessed;
        private final int tripsCreated;
        private final java.util.Map<String, Integer> tripsBySchedule;

        public AutoScheduleResponse(boolean success, String message, int schedulesProcessed, 
                               int tripsCreated, java.util.Map<String, Integer> tripsBySchedule) {
            this.success = success;
            this.message = message;
            this.schedulesProcessed = schedulesProcessed;
            this.tripsCreated = tripsCreated;
            this.tripsBySchedule = tripsBySchedule;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public int getSchedulesProcessed() {
            return schedulesProcessed;
        }

        public int getTripsCreated() {
            return tripsCreated;
        }

        public java.util.Map<String, Integer> getTripsBySchedule() {
            return tripsBySchedule;
        }
    }

    /**
     * Status class for auto-schedule system.
     */
    public static class AutoScheduleStatus {
        private final String status;
        private final String cronExpression;
        private final long timestamp;

        public AutoScheduleStatus(String status, String cronExpression, long timestamp) {
            this.status = status;
            this.cronExpression = cronExpression;
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public String getCronExpression() {
            return cronExpression;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}