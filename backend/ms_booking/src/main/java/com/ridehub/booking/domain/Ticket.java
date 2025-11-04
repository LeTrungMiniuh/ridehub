package com.ridehub.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ridehub.booking.domain.enumeration.AvroTicketStatus;
import com.ridehub.booking.domain.enumeration.ExchangeStatus;
import com.ridehub.booking.domain.enumeration.RefundStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Ticket.
 */
@Entity
@Table(name = "ticket")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 40)
    @Column(name = "ticket_code", length = 40, nullable = false, unique = true)
    private String ticketCode;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @Size(max = 256)
    @Column(name = "qr_code", length = 256)
    private String qrCode;

    @Column(name = "time_from")
    private Instant timeFrom;

    @Column(name = "time_to")
    private Instant timeTo;

    @Column(name = "checked_in")
    private Boolean checkedIn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AvroTicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_status")
    private ExchangeStatus exchangeStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status")
    private RefundStatus refundStatus;

    @Column(name = "exchange_reason")
    private String exchangeReason;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "exchange_requested_at")
    private Instant exchangeRequestedAt;

    @Column(name = "exchange_completed_at")
    private Instant exchangeCompletedAt;

    @Column(name = "refund_requested_at")
    private Instant refundRequestedAt;

    @Column(name = "refund_completed_at")
    private Instant refundCompletedAt;

    @Column(name = "refund_amount", precision = 21, scale = 2)
    private BigDecimal refundAmount;

    @Size(max = 80)
    @Column(name = "refund_transaction_id", length = 80)
    private String refundTransactionId;

    @NotNull
    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @NotNull
    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @NotNull
    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "deleted_by", length = 36)
    private UUID deletedBy;

    @JsonIgnoreProperties(value = { "ticket" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private FileBooking qrCodeImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "qrCodeImg", "originalTicket", "exchangedTicket", "booking", "exchangedFroms", "exchangedTos" },
        allowSetters = true
    )
    private Ticket originalTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "qrCodeImg", "originalTicket", "exchangedTicket", "booking", "exchangedFroms", "exchangedTos" },
        allowSetters = true
    )
    private Ticket exchangedTicket;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "invoice", "paymentTransaction", "tickets", "appliedPromos", "pricingSnapshots" }, allowSetters = true)
    private Booking booking;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originalTicket")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "qrCodeImg", "originalTicket", "exchangedTicket", "booking", "exchangedFroms", "exchangedTos" },
        allowSetters = true
    )
    private Set<Ticket> exchangedFroms = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exchangedTicket")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "qrCodeImg", "originalTicket", "exchangedTicket", "booking", "exchangedFroms", "exchangedTos" },
        allowSetters = true
    )
    private Set<Ticket> exchangedTos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ticket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketCode() {
        return this.ticketCode;
    }

    public Ticket ticketCode(String ticketCode) {
        this.setTicketCode(ticketCode);
        return this;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Ticket price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public Ticket qrCode(String qrCode) {
        this.setQrCode(qrCode);
        return this;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Instant getTimeFrom() {
        return this.timeFrom;
    }

    public Ticket timeFrom(Instant timeFrom) {
        this.setTimeFrom(timeFrom);
        return this;
    }

    public void setTimeFrom(Instant timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Instant getTimeTo() {
        return this.timeTo;
    }

    public Ticket timeTo(Instant timeTo) {
        this.setTimeTo(timeTo);
        return this;
    }

    public void setTimeTo(Instant timeTo) {
        this.timeTo = timeTo;
    }

    public Boolean getCheckedIn() {
        return this.checkedIn;
    }

    public Ticket checkedIn(Boolean checkedIn) {
        this.setCheckedIn(checkedIn);
        return this;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public AvroTicketStatus getStatus() {
        return this.status;
    }

    public Ticket status(AvroTicketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AvroTicketStatus status) {
        this.status = status;
    }

    public ExchangeStatus getExchangeStatus() {
        return this.exchangeStatus;
    }

    public Ticket exchangeStatus(ExchangeStatus exchangeStatus) {
        this.setExchangeStatus(exchangeStatus);
        return this;
    }

    public void setExchangeStatus(ExchangeStatus exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public RefundStatus getRefundStatus() {
        return this.refundStatus;
    }

    public Ticket refundStatus(RefundStatus refundStatus) {
        this.setRefundStatus(refundStatus);
        return this;
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getExchangeReason() {
        return this.exchangeReason;
    }

    public Ticket exchangeReason(String exchangeReason) {
        this.setExchangeReason(exchangeReason);
        return this;
    }

    public void setExchangeReason(String exchangeReason) {
        this.exchangeReason = exchangeReason;
    }

    public String getRefundReason() {
        return this.refundReason;
    }

    public Ticket refundReason(String refundReason) {
        this.setRefundReason(refundReason);
        return this;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public Instant getExchangeRequestedAt() {
        return this.exchangeRequestedAt;
    }

    public Ticket exchangeRequestedAt(Instant exchangeRequestedAt) {
        this.setExchangeRequestedAt(exchangeRequestedAt);
        return this;
    }

    public void setExchangeRequestedAt(Instant exchangeRequestedAt) {
        this.exchangeRequestedAt = exchangeRequestedAt;
    }

    public Instant getExchangeCompletedAt() {
        return this.exchangeCompletedAt;
    }

    public Ticket exchangeCompletedAt(Instant exchangeCompletedAt) {
        this.setExchangeCompletedAt(exchangeCompletedAt);
        return this;
    }

    public void setExchangeCompletedAt(Instant exchangeCompletedAt) {
        this.exchangeCompletedAt = exchangeCompletedAt;
    }

    public Instant getRefundRequestedAt() {
        return this.refundRequestedAt;
    }

    public Ticket refundRequestedAt(Instant refundRequestedAt) {
        this.setRefundRequestedAt(refundRequestedAt);
        return this;
    }

    public void setRefundRequestedAt(Instant refundRequestedAt) {
        this.refundRequestedAt = refundRequestedAt;
    }

    public Instant getRefundCompletedAt() {
        return this.refundCompletedAt;
    }

    public Ticket refundCompletedAt(Instant refundCompletedAt) {
        this.setRefundCompletedAt(refundCompletedAt);
        return this;
    }

    public void setRefundCompletedAt(Instant refundCompletedAt) {
        this.refundCompletedAt = refundCompletedAt;
    }

    public BigDecimal getRefundAmount() {
        return this.refundAmount;
    }

    public Ticket refundAmount(BigDecimal refundAmount) {
        this.setRefundAmount(refundAmount);
        return this;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundTransactionId() {
        return this.refundTransactionId;
    }

    public Ticket refundTransactionId(String refundTransactionId) {
        this.setRefundTransactionId(refundTransactionId);
        return this;
    }

    public void setRefundTransactionId(String refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }

    public Long getTripId() {
        return this.tripId;
    }

    public Ticket tripId(Long tripId) {
        this.setTripId(tripId);
        return this;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getRouteId() {
        return this.routeId;
    }

    public Ticket routeId(Long routeId) {
        this.setRouteId(routeId);
        return this;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getSeatId() {
        return this.seatId;
    }

    public Ticket seatId(Long seatId) {
        this.setSeatId(seatId);
        return this;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Ticket createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Ticket updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Ticket isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Ticket deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public Ticket deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public FileBooking getQrCodeImg() {
        return this.qrCodeImg;
    }

    public void setQrCodeImg(FileBooking fileBooking) {
        this.qrCodeImg = fileBooking;
    }

    public Ticket qrCodeImg(FileBooking fileBooking) {
        this.setQrCodeImg(fileBooking);
        return this;
    }

    public Ticket getOriginalTicket() {
        return this.originalTicket;
    }

    public void setOriginalTicket(Ticket ticket) {
        this.originalTicket = ticket;
    }

    public Ticket originalTicket(Ticket ticket) {
        this.setOriginalTicket(ticket);
        return this;
    }

    public Ticket getExchangedTicket() {
        return this.exchangedTicket;
    }

    public void setExchangedTicket(Ticket ticket) {
        this.exchangedTicket = ticket;
    }

    public Ticket exchangedTicket(Ticket ticket) {
        this.setExchangedTicket(ticket);
        return this;
    }

    public Booking getBooking() {
        return this.booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Ticket booking(Booking booking) {
        this.setBooking(booking);
        return this;
    }

    public Set<Ticket> getExchangedFroms() {
        return this.exchangedFroms;
    }

    public void setExchangedFroms(Set<Ticket> tickets) {
        if (this.exchangedFroms != null) {
            this.exchangedFroms.forEach(i -> i.setOriginalTicket(null));
        }
        if (tickets != null) {
            tickets.forEach(i -> i.setOriginalTicket(this));
        }
        this.exchangedFroms = tickets;
    }

    public Ticket exchangedFroms(Set<Ticket> tickets) {
        this.setExchangedFroms(tickets);
        return this;
    }

    public Ticket addExchangedFrom(Ticket ticket) {
        this.exchangedFroms.add(ticket);
        ticket.setOriginalTicket(this);
        return this;
    }

    public Ticket removeExchangedFrom(Ticket ticket) {
        this.exchangedFroms.remove(ticket);
        ticket.setOriginalTicket(null);
        return this;
    }

    public Set<Ticket> getExchangedTos() {
        return this.exchangedTos;
    }

    public void setExchangedTos(Set<Ticket> tickets) {
        if (this.exchangedTos != null) {
            this.exchangedTos.forEach(i -> i.setExchangedTicket(null));
        }
        if (tickets != null) {
            tickets.forEach(i -> i.setExchangedTicket(this));
        }
        this.exchangedTos = tickets;
    }

    public Ticket exchangedTos(Set<Ticket> tickets) {
        this.setExchangedTos(tickets);
        return this;
    }

    public Ticket addExchangedTo(Ticket ticket) {
        this.exchangedTos.add(ticket);
        ticket.setExchangedTicket(this);
        return this;
    }

    public Ticket removeExchangedTo(Ticket ticket) {
        this.exchangedTos.remove(ticket);
        ticket.setExchangedTicket(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return getId() != null && getId().equals(((Ticket) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ticket{" +
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
            "}";
    }
}
