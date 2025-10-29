package com.ridehub.booking.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.booking.domain.PricingSnapshot} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PricingSnapshotDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal baseFare;

    private BigDecimal vehicleFactor;

    private BigDecimal floorFactor;

    private BigDecimal seatFactor;

    private BigDecimal scheduleOccasionFactor;

    @NotNull
    private BigDecimal finalPrice;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    @NotNull
    private BookingDTO booking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getVehicleFactor() {
        return vehicleFactor;
    }

    public void setVehicleFactor(BigDecimal vehicleFactor) {
        this.vehicleFactor = vehicleFactor;
    }

    public BigDecimal getFloorFactor() {
        return floorFactor;
    }

    public void setFloorFactor(BigDecimal floorFactor) {
        this.floorFactor = floorFactor;
    }

    public BigDecimal getSeatFactor() {
        return seatFactor;
    }

    public void setSeatFactor(BigDecimal seatFactor) {
        this.seatFactor = seatFactor;
    }

    public BigDecimal getScheduleOccasionFactor() {
        return scheduleOccasionFactor;
    }

    public void setScheduleOccasionFactor(BigDecimal scheduleOccasionFactor) {
        this.scheduleOccasionFactor = scheduleOccasionFactor;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
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
        if (!(o instanceof PricingSnapshotDTO)) {
            return false;
        }

        PricingSnapshotDTO pricingSnapshotDTO = (PricingSnapshotDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pricingSnapshotDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PricingSnapshotDTO{" +
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
            ", booking=" + getBooking() +
            "}";
    }
}
