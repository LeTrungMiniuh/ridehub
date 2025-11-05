package com.ridehub.booking.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for ticket cancel request.
 */
public class TicketCancelRequestDTO implements Serializable {

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    public TicketCancelRequestDTO() {
    }

    public TicketCancelRequestDTO(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "TicketCancelRequestDTO{" +
            "reason='" + reason + '\'' +
            '}';
    }
}