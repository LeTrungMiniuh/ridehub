package com.ridehub.route.service.criteria;

import com.ridehub.route.domain.enumeration.OccasionType;
import com.ridehub.route.domain.enumeration.SeatType;
import com.ridehub.route.domain.enumeration.VehicleType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.PricingTemplate} entity. This class is used
 * in {@link com.ridehub.route.web.rest.PricingTemplateResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pricing-templates?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PricingTemplateCriteria implements Serializable, Criteria {

    /**
     * Class for filtering VehicleType
     */
    public static class VehicleTypeFilter extends Filter<VehicleType> {

        public VehicleTypeFilter() {}

        public VehicleTypeFilter(VehicleTypeFilter filter) {
            super(filter);
        }

        @Override
        public VehicleTypeFilter copy() {
            return new VehicleTypeFilter(this);
        }
    }

    /**
     * Class for filtering SeatType
     */
    public static class SeatTypeFilter extends Filter<SeatType> {

        public SeatTypeFilter() {}

        public SeatTypeFilter(SeatTypeFilter filter) {
            super(filter);
        }

        @Override
        public SeatTypeFilter copy() {
            return new SeatTypeFilter(this);
        }
    }

    /**
     * Class for filtering OccasionType
     */
    public static class OccasionTypeFilter extends Filter<OccasionType> {

        public OccasionTypeFilter() {}

        public OccasionTypeFilter(OccasionTypeFilter filter) {
            super(filter);
        }

        @Override
        public OccasionTypeFilter copy() {
            return new OccasionTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private VehicleTypeFilter vehicleType;

    private SeatTypeFilter seatType;

    private OccasionTypeFilter occasionType;

    private BigDecimalFilter baseFare;

    private BigDecimalFilter vehicleFactor;

    private BigDecimalFilter floorFactor;

    private BigDecimalFilter seatFactor;

    private BigDecimalFilter occasionFactor;

    private BigDecimalFilter finalPrice;

    private LocalDateFilter validFrom;

    private LocalDateFilter validTo;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter routeId;

    private Boolean distinct;

    public PricingTemplateCriteria() {}

    public PricingTemplateCriteria(PricingTemplateCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.vehicleType = other.optionalVehicleType().map(VehicleTypeFilter::copy).orElse(null);
        this.seatType = other.optionalSeatType().map(SeatTypeFilter::copy).orElse(null);
        this.occasionType = other.optionalOccasionType().map(OccasionTypeFilter::copy).orElse(null);
        this.baseFare = other.optionalBaseFare().map(BigDecimalFilter::copy).orElse(null);
        this.vehicleFactor = other.optionalVehicleFactor().map(BigDecimalFilter::copy).orElse(null);
        this.floorFactor = other.optionalFloorFactor().map(BigDecimalFilter::copy).orElse(null);
        this.seatFactor = other.optionalSeatFactor().map(BigDecimalFilter::copy).orElse(null);
        this.occasionFactor = other.optionalOccasionFactor().map(BigDecimalFilter::copy).orElse(null);
        this.finalPrice = other.optionalFinalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.validFrom = other.optionalValidFrom().map(LocalDateFilter::copy).orElse(null);
        this.validTo = other.optionalValidTo().map(LocalDateFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.routeId = other.optionalRouteId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PricingTemplateCriteria copy() {
        return new PricingTemplateCriteria(this);
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

    public VehicleTypeFilter getVehicleType() {
        return vehicleType;
    }

    public Optional<VehicleTypeFilter> optionalVehicleType() {
        return Optional.ofNullable(vehicleType);
    }

    public VehicleTypeFilter vehicleType() {
        if (vehicleType == null) {
            setVehicleType(new VehicleTypeFilter());
        }
        return vehicleType;
    }

    public void setVehicleType(VehicleTypeFilter vehicleType) {
        this.vehicleType = vehicleType;
    }

    public SeatTypeFilter getSeatType() {
        return seatType;
    }

    public Optional<SeatTypeFilter> optionalSeatType() {
        return Optional.ofNullable(seatType);
    }

    public SeatTypeFilter seatType() {
        if (seatType == null) {
            setSeatType(new SeatTypeFilter());
        }
        return seatType;
    }

    public void setSeatType(SeatTypeFilter seatType) {
        this.seatType = seatType;
    }

    public OccasionTypeFilter getOccasionType() {
        return occasionType;
    }

    public Optional<OccasionTypeFilter> optionalOccasionType() {
        return Optional.ofNullable(occasionType);
    }

    public OccasionTypeFilter occasionType() {
        if (occasionType == null) {
            setOccasionType(new OccasionTypeFilter());
        }
        return occasionType;
    }

    public void setOccasionType(OccasionTypeFilter occasionType) {
        this.occasionType = occasionType;
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

    public BigDecimalFilter getOccasionFactor() {
        return occasionFactor;
    }

    public Optional<BigDecimalFilter> optionalOccasionFactor() {
        return Optional.ofNullable(occasionFactor);
    }

    public BigDecimalFilter occasionFactor() {
        if (occasionFactor == null) {
            setOccasionFactor(new BigDecimalFilter());
        }
        return occasionFactor;
    }

    public void setOccasionFactor(BigDecimalFilter occasionFactor) {
        this.occasionFactor = occasionFactor;
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

    public LocalDateFilter getValidFrom() {
        return validFrom;
    }

    public Optional<LocalDateFilter> optionalValidFrom() {
        return Optional.ofNullable(validFrom);
    }

    public LocalDateFilter validFrom() {
        if (validFrom == null) {
            setValidFrom(new LocalDateFilter());
        }
        return validFrom;
    }

    public void setValidFrom(LocalDateFilter validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateFilter getValidTo() {
        return validTo;
    }

    public Optional<LocalDateFilter> optionalValidTo() {
        return Optional.ofNullable(validTo);
    }

    public LocalDateFilter validTo() {
        if (validTo == null) {
            setValidTo(new LocalDateFilter());
        }
        return validTo;
    }

    public void setValidTo(LocalDateFilter validTo) {
        this.validTo = validTo;
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

    public LongFilter getRouteId() {
        return routeId;
    }

    public Optional<LongFilter> optionalRouteId() {
        return Optional.ofNullable(routeId);
    }

    public LongFilter routeId() {
        if (routeId == null) {
            setRouteId(new LongFilter());
        }
        return routeId;
    }

    public void setRouteId(LongFilter routeId) {
        this.routeId = routeId;
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
        final PricingTemplateCriteria that = (PricingTemplateCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(vehicleType, that.vehicleType) &&
            Objects.equals(seatType, that.seatType) &&
            Objects.equals(occasionType, that.occasionType) &&
            Objects.equals(baseFare, that.baseFare) &&
            Objects.equals(vehicleFactor, that.vehicleFactor) &&
            Objects.equals(floorFactor, that.floorFactor) &&
            Objects.equals(seatFactor, that.seatFactor) &&
            Objects.equals(occasionFactor, that.occasionFactor) &&
            Objects.equals(finalPrice, that.finalPrice) &&
            Objects.equals(validFrom, that.validFrom) &&
            Objects.equals(validTo, that.validTo) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(routeId, that.routeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            vehicleType,
            seatType,
            occasionType,
            baseFare,
            vehicleFactor,
            floorFactor,
            seatFactor,
            occasionFactor,
            finalPrice,
            validFrom,
            validTo,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            routeId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PricingTemplateCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalVehicleType().map(f -> "vehicleType=" + f + ", ").orElse("") +
            optionalSeatType().map(f -> "seatType=" + f + ", ").orElse("") +
            optionalOccasionType().map(f -> "occasionType=" + f + ", ").orElse("") +
            optionalBaseFare().map(f -> "baseFare=" + f + ", ").orElse("") +
            optionalVehicleFactor().map(f -> "vehicleFactor=" + f + ", ").orElse("") +
            optionalFloorFactor().map(f -> "floorFactor=" + f + ", ").orElse("") +
            optionalSeatFactor().map(f -> "seatFactor=" + f + ", ").orElse("") +
            optionalOccasionFactor().map(f -> "occasionFactor=" + f + ", ").orElse("") +
            optionalFinalPrice().map(f -> "finalPrice=" + f + ", ").orElse("") +
            optionalValidFrom().map(f -> "validFrom=" + f + ", ").orElse("") +
            optionalValidTo().map(f -> "validTo=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalRouteId().map(f -> "routeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
