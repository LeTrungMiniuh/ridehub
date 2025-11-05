package com.ridehub.booking.service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for ticket refund request.
 */
public class TicketRefundRequestDTO implements Serializable {

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private BigDecimal refundAmount;

    public TicketRefundRequestDTO() {
    }

    public TicketRefundRequestDTO(String reason, BigDecimal refundAmount) {
        this.reason = reason;
        this.refundAmount = refundAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    @Override
    public String toString() {
        return "TicketRefundRequestDTO{" +
            "reason='" + reason + '\'' +
            ", refundAmount=" + refundAmount +
            '}';
    }
}