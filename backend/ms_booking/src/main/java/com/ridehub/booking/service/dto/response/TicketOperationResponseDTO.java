package com.ridehub.booking.service.dto.response;

import com.ridehub.booking.service.dto.TicketDTO;
import java.io.Serializable;

/**
 * A DTO for ticket operation response.
 */
public class TicketOperationResponseDTO implements Serializable {

    private TicketDTO ticket;
    private String message;
    private Boolean success;

    public TicketOperationResponseDTO() {
    }

    public TicketOperationResponseDTO(TicketDTO ticket, String message, Boolean success) {
        this.ticket = ticket;
        this.message = message;
        this.success = success;
    }

    public TicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "TicketOperationResponseDTO{" +
            "ticket=" + ticket +
            ", message='" + message + '\'' +
            ", success=" + success +
            '}';
    }
}