package com.ridehub.booking.service.dto;

import com.ridehub.booking.domain.enumeration.AvroTicketStatus;
import com.ridehub.booking.domain.enumeration.ExchangeStatus;
import com.ridehub.booking.domain.enumeration.RefundStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.booking.domain.Ticket} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 40)
    private String ticketCode;

    @NotNull
    private BigDecimal price;

    @Size(max = 256)
    private String qrCode;

    private Instant timeFrom;

    private Instant timeTo;

    private Boolean checkedIn;

    @NotNull
    private AvroTicketStatus status;

    private ExchangeStatus exchangeStatus;

    private RefundStatus refundStatus;

    private String exchangeReason;

    private String refundReason;

    private Instant exchangeRequestedAt;

    private Instant exchangeCompletedAt;

    private Instant refundRequestedAt;

    private Instant refundCompletedAt;

    private BigDecimal refundAmount;

    @Size(max = 80)
    private String refundTransactionId;

    @NotNull
    private Long tripId;

    @NotNull
    private Long routeId;

    @NotNull
    private Long seatId;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    private FileBookingDTO qrCodeImg;

    private TicketDTO originalTicket;

    private TicketDTO exchangedTicket;

    @NotNull
    private BookingDTO booking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Instant getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Instant timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Instant getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Instant timeTo) {
        this.timeTo = timeTo;
    }

    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public AvroTicketStatus getStatus() {
        return status;
    }

    public void setStatus(AvroTicketStatus status) {
        this.status = status;
    }

    public ExchangeStatus getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(ExchangeStatus exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public RefundStatus getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getExchangeReason() {
        return exchangeReason;
    }

    public void setExchangeReason(String exchangeReason) {
        this.exchangeReason = exchangeReason;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public Instant getExchangeRequestedAt() {
        return exchangeRequestedAt;
    }

    public void setExchangeRequestedAt(Instant exchangeRequestedAt) {
        this.exchangeRequestedAt = exchangeRequestedAt;
    }

    public Instant getExchangeCompletedAt() {
        return exchangeCompletedAt;
    }

    public void setExchangeCompletedAt(Instant exchangeCompletedAt) {
        this.exchangeCompletedAt = exchangeCompletedAt;
    }

    public Instant getRefundRequestedAt() {
        return refundRequestedAt;
    }

    public void setRefundRequestedAt(Instant refundRequestedAt) {
        this.refundRequestedAt = refundRequestedAt;
    }

    public Instant getRefundCompletedAt() {
        return refundCompletedAt;
    }

    public void setRefundCompletedAt(Instant refundCompletedAt) {
        this.refundCompletedAt = refundCompletedAt;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundTransactionId() {
        return refundTransactionId;
    }

    public void setRefundTransactionId(String refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public FileBookingDTO getQrCodeImg() {
        return qrCodeImg;
    }

    public void setQrCodeImg(FileBookingDTO qrCodeImg) {
        this.qrCodeImg = qrCodeImg;
    }

    public TicketDTO getOriginalTicket() {
        return originalTicket;
    }

    public void setOriginalTicket(TicketDTO originalTicket) {
        this.originalTicket = originalTicket;
    }

    public TicketDTO getExchangedTicket() {
        return exchangedTicket;
    }

    public void setExchangedTicket(TicketDTO exchangedTicket) {
        this.exchangedTicket = exchangedTicket;
    }

    public BookingDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingDTO booking) {
        this.booking = booking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketDTO)) {
            return false;
        }

        TicketDTO ticketDTO = (TicketDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDTO{" +
            "id=" + getId() +
            ", ticketCode='" + getTicketCode() + "'" +
            ", price=" + getPrice() +
            ", qrCode='" + getQrCode() + "'" +
            ", timeFrom='" + getTimeFrom() + "'" +
            ", timeTo='" + getTimeTo() + "'" +
            ", checkedIn='" + getCheckedIn() + "'" +
            ", status='" + getStatus() + "'" +
            ", exchangeStatus='" + getExchangeStatus() + "'" +
            ", refundStatus='" + getRefundStatus() + "'" +
            ", exchangeReason='" + getExchangeReason() + "'" +
            ", refundReason='" + getRefundReason() + "'" +
            ", exchangeRequestedAt='" + getExchangeRequestedAt() + "'" +
            ", exchangeCompletedAt='" + getExchangeCompletedAt() + "'" +
            ", refundRequestedAt='" + getRefundRequestedAt() + "'" +
            ", refundCompletedAt='" + getRefundCompletedAt() + "'" +
            ", refundAmount=" + getRefundAmount() +
            ", refundTransactionId='" + getRefundTransactionId() + "'" +
            ", tripId=" + getTripId() +
            ", routeId=" + getRouteId() +
            ", seatId=" + getSeatId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", qrCodeImg=" + getQrCodeImg() +
            ", originalTicket=" + getOriginalTicket() +
            ", exchangedTicket=" + getExchangedTicket() +
            ", booking=" + getBooking() +
            "}";
    }
}
