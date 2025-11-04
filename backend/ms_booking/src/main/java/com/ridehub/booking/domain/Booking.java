package com.ridehub.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ridehub.booking.domain.enumeration.BookingStatus;
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
 * A Booking.
 */
@Entity
@Table(name = "booking")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "booking_code", nullable = false, unique = true)
    private String bookingCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "booked_at", nullable = false)
    private Instant bookedAt;

    @NotNull
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "customer_id", length = 36, nullable = false)
    private UUID customerId;

    @Size(max = 80)
    @Column(name = "idempotency_key", length = 80)
    private String idempotencyKey;

    @NotNull
    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Size(max = 36)
    @Column(name = "lock_group_id", length = 36)
    private String lockGroupId;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "timeout_minutes")
    private Integer timeoutMinutes;

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

    @JsonIgnoreProperties(value = { "booking" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Invoice invoice;

    @JsonIgnoreProperties(value = { "webhooks", "booking" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private PaymentTransaction paymentTransaction;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "booking")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "qrCodeImg", "originalTicket", "exchangedTicket", "booking", "exchangedFroms", "exchangedTos" },
        allowSetters = true
    )
    private Set<Ticket> tickets = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "booking")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "booking" }, allowSetters = true)
    private Set<AppliedPromotion> appliedPromos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "booking")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "booking" }, allowSetters = true)
    private Set<PricingSnapshot> pricingSnapshots = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Booking id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingCode() {
        return this.bookingCode;
    }

    public Booking bookingCode(String bookingCode) {
        this.setBookingCode(bookingCode);
        return this;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public BookingStatus getStatus() {
        return this.status;
    }

    public Booking status(BookingStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Booking quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Booking totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getBookedAt() {
        return this.bookedAt;
    }

    public Booking bookedAt(Instant bookedAt) {
        this.setBookedAt(bookedAt);
        return this;
    }

    public void setBookedAt(Instant bookedAt) {
        this.bookedAt = bookedAt;
    }

    public UUID getCustomerId() {
        return this.customerId;
    }

    public Booking customerId(UUID customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public Booking idempotencyKey(String idempotencyKey) {
        this.setIdempotencyKey(idempotencyKey);
        return this;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getTripId() {
        return this.tripId;
    }

    public Booking tripId(Long tripId) {
        this.setTripId(tripId);
        return this;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getLockGroupId() {
        return this.lockGroupId;
    }

    public Booking lockGroupId(String lockGroupId) {
        this.setLockGroupId(lockGroupId);
        return this;
    }

    public void setLockGroupId(String lockGroupId) {
        this.lockGroupId = lockGroupId;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public Booking expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getTimeoutMinutes() {
        return this.timeoutMinutes;
    }

    public Booking timeoutMinutes(Integer timeoutMinutes) {
        this.setTimeoutMinutes(timeoutMinutes);
        return this;
    }

    public void setTimeoutMinutes(Integer timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Booking createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Booking updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Booking isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Booking deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public Booking deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Booking invoice(Invoice invoice) {
        this.setInvoice(invoice);
        return this;
    }

    public PaymentTransaction getPaymentTransaction() {
        return this.paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public Booking paymentTransaction(PaymentTransaction paymentTransaction) {
        this.setPaymentTransaction(paymentTransaction);
        return this;
    }

    public Set<Ticket> getTickets() {
        return this.tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        if (this.tickets != null) {
            this.tickets.forEach(i -> i.setBooking(null));
        }
        if (tickets != null) {
            tickets.forEach(i -> i.setBooking(this));
        }
        this.tickets = tickets;
    }

    public Booking tickets(Set<Ticket> tickets) {
        this.setTickets(tickets);
        return this;
    }

    public Booking addTickets(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setBooking(this);
        return this;
    }

    public Booking removeTickets(Ticket ticket) {
        this.tickets.remove(ticket);
        ticket.setBooking(null);
        return this;
    }

    public Set<AppliedPromotion> getAppliedPromos() {
        return this.appliedPromos;
    }

    public void setAppliedPromos(Set<AppliedPromotion> appliedPromotions) {
        if (this.appliedPromos != null) {
            this.appliedPromos.forEach(i -> i.setBooking(null));
        }
        if (appliedPromotions != null) {
            appliedPromotions.forEach(i -> i.setBooking(this));
        }
        this.appliedPromos = appliedPromotions;
    }

    public Booking appliedPromos(Set<AppliedPromotion> appliedPromotions) {
        this.setAppliedPromos(appliedPromotions);
        return this;
    }

    public Booking addAppliedPromos(AppliedPromotion appliedPromotion) {
        this.appliedPromos.add(appliedPromotion);
        appliedPromotion.setBooking(this);
        return this;
    }

    public Booking removeAppliedPromos(AppliedPromotion appliedPromotion) {
        this.appliedPromos.remove(appliedPromotion);
        appliedPromotion.setBooking(null);
        return this;
    }

    public Set<PricingSnapshot> getPricingSnapshots() {
        return this.pricingSnapshots;
    }

    public void setPricingSnapshots(Set<PricingSnapshot> pricingSnapshots) {
        if (this.pricingSnapshots != null) {
            this.pricingSnapshots.forEach(i -> i.setBooking(null));
        }
        if (pricingSnapshots != null) {
            pricingSnapshots.forEach(i -> i.setBooking(this));
        }
        this.pricingSnapshots = pricingSnapshots;
    }

    public Booking pricingSnapshots(Set<PricingSnapshot> pricingSnapshots) {
        this.setPricingSnapshots(pricingSnapshots);
        return this;
    }

    public Booking addPricingSnapshots(PricingSnapshot pricingSnapshot) {
        this.pricingSnapshots.add(pricingSnapshot);
        pricingSnapshot.setBooking(this);
        return this;
    }

    public Booking removePricingSnapshots(PricingSnapshot pricingSnapshot) {
        this.pricingSnapshots.remove(pricingSnapshot);
        pricingSnapshot.setBooking(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return getId() != null && getId().equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Booking{" +
            "id=" + getId() +
            ", bookingCode='" + getBookingCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", quantity=" + getQuantity() +
            ", totalAmount=" + getTotalAmount() +
            ", bookedAt='" + getBookedAt() + "'" +
            ", customerId='" + getCustomerId() + "'" +
            ", idempotencyKey='" + getIdempotencyKey() + "'" +
            ", tripId=" + getTripId() +
            ", lockGroupId='" + getLockGroupId() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", timeoutMinutes=" + getTimeoutMinutes() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
