package com.ridehub.booking.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for ticket exchange request.
 */
public class TicketExchangeRequestDTO implements Serializable {

    @NotNull(message = "New trip ID is required")
    private Long newTripId;

    @NotNull(message = "New route ID is required")
    private Long newRouteId;

    @NotNull(message = "New seat ID is required")
    private Long newSeatId;

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    public TicketExchangeRequestDTO() {
    }

    public TicketExchangeRequestDTO(Long newTripId, Long newRouteId, Long newSeatId, String reason) {
        this.newTripId = newTripId;
        this.newRouteId = newRouteId;
        this.newSeatId = newSeatId;
        this.reason = reason;
    }

    public Long getNewTripId() {
        return newTripId;
    }

    public void setNewTripId(Long newTripId) {
        this.newTripId = newTripId;
    }

    public Long getNewRouteId() {
        return newRouteId;
    }

    public void setNewRouteId(Long newRouteId) {
        this.newRouteId = newRouteId;
    }

    public Long getNewSeatId() {
        return newSeatId;
    }

    public void setNewSeatId(Long newSeatId) {
        this.newSeatId = newSeatId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "TicketExchangeRequestDTO{" +
            "newTripId=" + newTripId +
            ", newRouteId=" + newRouteId +
            ", newSeatId=" + newSeatId +
            ", reason='" + reason + '\'' +
            '}';
    }
}