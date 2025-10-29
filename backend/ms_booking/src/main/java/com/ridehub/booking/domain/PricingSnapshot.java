package com.ridehub.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A PricingSnapshot.
 */
@Entity
@Table(name = "pricing_snapshot")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PricingSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "base_fare", precision = 21, scale = 2, nullable = false)
    private BigDecimal baseFare;

    @Column(name = "vehicle_factor", precision = 21, scale = 2)
    private BigDecimal vehicleFactor;

    @Column(name = "floor_factor", precision = 21, scale = 2)
    private BigDecimal floorFactor;

    @Column(name = "seat_factor", precision = 21, scale = 2)
    private BigDecimal seatFactor;

    @Column(name = "schedule_occasion_factor", precision = 21, scale = 2)
    private BigDecimal scheduleOccasionFactor;

    @NotNull
    @Column(name = "final_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal finalPrice;

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

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "invoice", "paymentTransaction", "tickets", "appliedPromos", "pricingSnapshots" }, allowSetters = true)
    private Booking booking;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PricingSnapshot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBaseFare() {
        return this.baseFare;
    }

    public PricingSnapshot baseFare(BigDecimal baseFare) {
        this.setBaseFare(baseFare);
        return this;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getVehicleFactor() {
        return this.vehicleFactor;
    }

    public PricingSnapshot vehicleFactor(BigDecimal vehicleFactor) {
        this.setVehicleFactor(vehicleFactor);
        return this;
    }

    public void setVehicleFactor(BigDecimal vehicleFactor) {
        this.vehicleFactor = vehicleFactor;
    }

    public BigDecimal getFloorFactor() {
        return this.floorFactor;
    }

    public PricingSnapshot floorFactor(BigDecimal floorFactor) {
        this.setFloorFactor(floorFactor);
        return this;
    }

    public void setFloorFactor(BigDecimal floorFactor) {
        this.floorFactor = floorFactor;
    }

    public BigDecimal getSeatFactor() {
        return this.seatFactor;
    }

    public PricingSnapshot seatFactor(BigDecimal seatFactor) {
        this.setSeatFactor(seatFactor);
        return this;
    }

    public void setSeatFactor(BigDecimal seatFactor) {
        this.seatFactor = seatFactor;
    }

    public BigDecimal getScheduleOccasionFactor() {
        return this.scheduleOccasionFactor;
    }

    public PricingSnapshot scheduleOccasionFactor(BigDecimal scheduleOccasionFactor) {
        this.setScheduleOccasionFactor(scheduleOccasionFactor);
        return this;
    }

    public void setScheduleOccasionFactor(BigDecimal scheduleOccasionFactor) {
        this.scheduleOccasionFactor = scheduleOccasionFactor;
    }

    public BigDecimal getFinalPrice() {
        return this.finalPrice;
    }

    public PricingSnapshot finalPrice(BigDecimal finalPrice) {
        this.setFinalPrice(finalPrice);
        return this;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PricingSnapshot createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PricingSnapshot updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public PricingSnapshot isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public PricingSnapshot deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public PricingSnapshot deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Booking getBooking() {
        return this.booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public PricingSnapshot booking(Booking booking) {
        this.setBooking(booking);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PricingSnapshot)) {
            return false;
        }
        return getId() != null && getId().equals(((PricingSnapshot) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PricingSnapshot{" +
            "id=" + getId() +
            ", baseFare=" + getBaseFare() +
            ", vehicleFactor=" + getVehicleFactor() +
            ", floorFactor=" + getFloorFactor() +
            ", seatFactor=" + getSeatFactor() +
            ", scheduleOccasionFactor=" + getScheduleOccasionFactor() +
            ", finalPrice=" + getFinalPrice() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
