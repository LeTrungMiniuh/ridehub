package com.ridehub.booking.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.booking.domain.PricingSnapshot} entity. This class is used
 * in {@link com.ridehub.booking.web.rest.PricingSnapshotResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pricing-snapshots?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PricingSnapshotCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter baseFare;

    private BigDecimalFilter vehicleFactor;

    private BigDecimalFilter floorFactor;

    private BigDecimalFilter seatFactor;

    private BigDecimalFilter scheduleOccasionFactor;

    private BigDecimalFilter finalPrice;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter bookingId;

    private Boolean distinct;

    public PricingSnapshotCriteria() {}

    public PricingSnapshotCriteria(PricingSnapshotCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.baseFare = other.optionalBaseFare().map(BigDecimalFilter::copy).orElse(null);
        this.vehicleFactor = other.optionalVehicleFactor().map(BigDecimalFilter::copy).orElse(null);
        this.floorFactor = other.optionalFloorFactor().map(BigDecimalFilter::copy).orElse(null);
        this.seatFactor = other.optionalSeatFactor().map(BigDecimalFilter::copy).orElse(null);
        this.scheduleOccasionFactor = other.optionalScheduleOccasionFactor().map(BigDecimalFilter::copy).orElse(null);
        this.finalPrice = other.optionalFinalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.bookingId = other.optionalBookingId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PricingSnapshotCriteria copy() {
        return new PricingSnapshotCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getBaseFare() {
        return baseFare;
    }

    public Optional<BigDecimalFilter> optionalBaseFare() {
        return Optional.ofNullable(baseFare);
    }

    public BigDecimalFilter baseFare() {
        if (baseFare == null) {
            setBaseFare(new BigDecimalFilter());
        }
        return baseFare;
    }

    public void setBaseFare(BigDecimalFilter baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimalFilter getVehicleFactor() {
        return vehicleFactor;
    }

    public Optional<BigDecimalFilter> optionalVehicleFactor() {
        return Optional.ofNullable(vehicleFactor);
    }

    public BigDecimalFilter vehicleFactor() {
        if (vehicleFactor == null) {
            setVehicleFactor(new BigDecimalFilter());
        }
        return vehicleFactor;
    }

    public void setVehicleFactor(BigDecimalFilter vehicleFactor) {
        this.vehicleFactor = vehicleFactor;
    }

    public BigDecimalFilter getFloorFactor() {
        return floorFactor;
    }

    public Optional<BigDecimalFilter> optionalFloorFactor() {
        return Optional.ofNullable(floorFactor);
    }

    public BigDecimalFilter floorFactor() {
        if (floorFactor == null) {
            setFloorFactor(new BigDecimalFilter());
        }
        return floorFactor;
    }

    public void setFloorFactor(BigDecimalFilter floorFactor) {
        this.floorFactor = floorFactor;
    }

    public BigDecimalFilter getSeatFactor() {
        return seatFactor;
    }

    public Optional<BigDecimalFilter> optionalSeatFactor() {
        return Optional.ofNullable(seatFactor);
    }

    public BigDecimalFilter seatFactor() {
        if (seatFactor == null) {
            setSeatFactor(new BigDecimalFilter());
        }
        return seatFactor;
    }

    public void setSeatFactor(BigDecimalFilter seatFactor) {
        this.seatFactor = seatFactor;
    }

    public BigDecimalFilter getScheduleOccasionFactor() {
        return scheduleOccasionFactor;
    }

    public Optional<BigDecimalFilter> optionalScheduleOccasionFactor() {
        return Optional.ofNullable(scheduleOccasionFactor);
    }

    public BigDecimalFilter scheduleOccasionFactor() {
        if (scheduleOccasionFactor == null) {
            setScheduleOccasionFactor(new BigDecimalFilter());
        }
        return scheduleOccasionFactor;
    }

    public void setScheduleOccasionFactor(BigDecimalFilter scheduleOccasionFactor) {
        this.scheduleOccasionFactor = scheduleOccasionFactor;
    }

    public BigDecimalFilter getFinalPrice() {
        return finalPrice;
    }

    public Optional<BigDecimalFilter> optionalFinalPrice() {
        return Optional.ofNullable(finalPrice);
    }

    public BigDecimalFilter finalPrice() {
        if (finalPrice == null) {
            setFinalPrice(new BigDecimalFilter());
        }
        return finalPrice;
    }

    public void setFinalPrice(BigDecimalFilter finalPrice) {
        this.finalPrice = finalPrice;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public InstantFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<InstantFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public InstantFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new InstantFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUIDFilter getDeletedBy() {
        return deletedBy;
    }

    public Optional<UUIDFilter> optionalDeletedBy() {
        return Optional.ofNullable(deletedBy);
    }

    public UUIDFilter deletedBy() {
        if (deletedBy == null) {
            setDeletedBy(new UUIDFilter());
        }
        return deletedBy;
    }

    public void setDeletedBy(UUIDFilter deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LongFilter getBookingId() {
        return bookingId;
    }

    public Optional<LongFilter> optionalBookingId() {
        return Optional.ofNullable(bookingId);
    }

    public LongFilter bookingId() {
        if (bookingId == null) {
            setBookingId(new LongFilter());
        }
        return bookingId;
    }

    public void setBookingId(LongFilter bookingId) {
        this.bookingId = bookingId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PricingSnapshotCriteria that = (PricingSnapshotCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(baseFare, that.baseFare) &&
            Objects.equals(vehicleFactor, that.vehicleFactor) &&
            Objects.equals(floorFactor, that.floorFactor) &&
            Objects.equals(seatFactor, that.seatFactor) &&
            Objects.equals(scheduleOccasionFactor, that.scheduleOccasionFactor) &&
            Objects.equals(finalPrice, that.finalPrice) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(bookingId, that.bookingId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            baseFare,
            vehicleFactor,
            floorFactor,
            seatFactor,
            scheduleOccasionFactor,
            finalPrice,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            bookingId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PricingSnapshotCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBaseFare().map(f -> "baseFare=" + f + ", ").orElse("") +
            optionalVehicleFactor().map(f -> "vehicleFactor=" + f + ", ").orElse("") +
            optionalFloorFactor().map(f -> "floorFactor=" + f + ", ").orElse("") +
            optionalSeatFactor().map(f -> "seatFactor=" + f + ", ").orElse("") +
            optionalScheduleOccasionFactor().map(f -> "scheduleOccasionFactor=" + f + ", ").orElse("") +
            optionalFinalPrice().map(f -> "finalPrice=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalBookingId().map(f -> "bookingId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
