package com.ridehub.route.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;

import com.ridehub.route.service.criteria.VehicleCriteria.VehicleTypeFilter;

import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.Trip} entity. This
 * class is used
 * in {@link com.ridehub.route.web.rest.TripResource} to receive all the
 * possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trips?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific
 * {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter tripCode;

    private InstantFilter departureTime;

    private InstantFilter arrivalTime;

    private BigDecimalFilter occasionFactor;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter routeId;

    private LongFilter vehicleId;

    private LongFilter slotId;

    private LongFilter driverId;

    private LongFilter attendantId;

    private VehicleTypeFilter vehicleType;

    private StringFilter routeCode;

    private StringFilter originDistrictCode;

    private StringFilter originProvinceCode;

    private StringFilter destinationDistrictCode;

    private StringFilter destinationProvinceCode;

    private StringFilter vehiclePlateNumber;

    private Boolean distinct;

    public TripCriteria() {
    }

    public TripCriteria(TripCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.tripCode = other.optionalTripCode().map(StringFilter::copy).orElse(null);
        this.departureTime = other.optionalDepartureTime().map(InstantFilter::copy).orElse(null);
        this.arrivalTime = other.optionalArrivalTime().map(InstantFilter::copy).orElse(null);
        this.occasionFactor = other.optionalOccasionFactor().map(BigDecimalFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.routeId = other.optionalRouteId().map(LongFilter::copy).orElse(null);
        this.vehicleId = other.optionalVehicleId().map(LongFilter::copy).orElse(null);
        this.slotId = other.optionalSlotId().map(LongFilter::copy).orElse(null);
        this.driverId = other.optionalDriverId().map(LongFilter::copy).orElse(null);
        this.attendantId = other.optionalAttendantId().map(LongFilter::copy).orElse(null);
        this.vehicleType = other.optionalVehicleType().map(VehicleTypeFilter::copy).orElse(null);

        this.routeCode = other.optionalRouteCode().map(StringFilter::copy).orElse(null);
        this.originDistrictCode = other.optionalOriginDistrictCode().map(StringFilter::copy).orElse(null);
        this.originProvinceCode = other.optionalOriginProvinceCode().map(StringFilter::copy).orElse(null);
        this.destinationDistrictCode = other.optionalDestinationDistrictCode().map(StringFilter::copy).orElse(null);
        this.destinationProvinceCode = other.optionalDestinationProvinceCode().map(StringFilter::copy).orElse(null);
        this.vehiclePlateNumber = other.optionalVehiclePlateNumber().map(StringFilter::copy).orElse(null);

        this.distinct = other.distinct;
    }

    @Override
    public TripCriteria copy() {
        return new TripCriteria(this);
    }

    public StringFilter getRouteCode() {
        if (routeCode == null)
            routeCode = new StringFilter();
        return routeCode;
    }

    public void setRouteCode(StringFilter routeCode) {
        this.routeCode = routeCode;
    }

    public Optional<StringFilter> optionalRouteCode() {
        return Optional.ofNullable(routeCode);
    }

    public StringFilter getOriginDistrictCode() {
        if (originDistrictCode == null)
            originDistrictCode = new StringFilter();
        return originDistrictCode;
    }

    public void setOriginDistrictCode(StringFilter f) {
        this.originDistrictCode = f;
    }

    public Optional<StringFilter> optionalOriginDistrictCode() {
        return Optional.ofNullable(originDistrictCode);
    }

    public StringFilter getOriginProvinceCode() {
        if (originProvinceCode == null)
            originProvinceCode = new StringFilter();
        return originProvinceCode;
    }

    public void setOriginProvinceCode(StringFilter f) {
        this.originProvinceCode = f;
    }

    public Optional<StringFilter> optionalOriginProvinceCode() {
        return Optional.ofNullable(originProvinceCode);
    }

    public StringFilter getDestinationDistrictCode() {
        if (destinationDistrictCode == null)
            destinationDistrictCode = new StringFilter();
        return destinationDistrictCode;
    }

    public void setDestinationDistrictCode(StringFilter f) {
        this.destinationDistrictCode = f;
    }

    public Optional<StringFilter> optionalDestinationDistrictCode() {
        return Optional.ofNullable(destinationDistrictCode);
    }

    public StringFilter getDestinationProvinceCode() {
        if (destinationProvinceCode == null)
            destinationProvinceCode = new StringFilter();
        return destinationProvinceCode;
    }

    public void setDestinationProvinceCode(StringFilter f) {
        this.destinationProvinceCode = f;
    }

    public Optional<StringFilter> optionalDestinationProvinceCode() {
        return Optional.ofNullable(destinationProvinceCode);
    }

    public StringFilter getVehiclePlateNumber() {
        if (vehiclePlateNumber == null)
            vehiclePlateNumber = new StringFilter();
        return vehiclePlateNumber;
    }

    public void setVehiclePlateNumber(StringFilter vehiclePlateNumber) {
        this.vehiclePlateNumber = vehiclePlateNumber;
    }

    public Optional<StringFilter> optionalVehiclePlateNumber() {
        return Optional.ofNullable(vehiclePlateNumber);
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

    public StringFilter getTripCode() {
        return tripCode;
    }

    public Optional<StringFilter> optionalTripCode() {
        return Optional.ofNullable(tripCode);
    }

    public StringFilter tripCode() {
        if (tripCode == null) {
            setTripCode(new StringFilter());
        }
        return tripCode;
    }

    public void setTripCode(StringFilter tripCode) {
        this.tripCode = tripCode;
    }

    public InstantFilter getDepartureTime() {
        return departureTime;
    }

    public Optional<InstantFilter> optionalDepartureTime() {
        return Optional.ofNullable(departureTime);
    }

    public InstantFilter departureTime() {
        if (departureTime == null) {
            setDepartureTime(new InstantFilter());
        }
        return departureTime;
    }

    public void setDepartureTime(InstantFilter departureTime) {
        this.departureTime = departureTime;
    }

    public InstantFilter getArrivalTime() {
        return arrivalTime;
    }

    public Optional<InstantFilter> optionalArrivalTime() {
        return Optional.ofNullable(arrivalTime);
    }

    public InstantFilter arrivalTime() {
        if (arrivalTime == null) {
            setArrivalTime(new InstantFilter());
        }
        return arrivalTime;
    }

    public void setArrivalTime(InstantFilter arrivalTime) {
        this.arrivalTime = arrivalTime;
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

    public LongFilter getVehicleId() {
        return vehicleId;
    }

    public Optional<LongFilter> optionalVehicleId() {
        return Optional.ofNullable(vehicleId);
    }

    public LongFilter vehicleId() {
        if (vehicleId == null) {
            setVehicleId(new LongFilter());
        }
        return vehicleId;
    }

    public void setVehicleId(LongFilter vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LongFilter getSlotId() {
        return slotId;
    }

    public Optional<LongFilter> optionalSlotId() {
        return Optional.ofNullable(slotId);
    }

    public LongFilter slotId() {
        if (slotId == null) {
            setSlotId(new LongFilter());
        }
        return slotId;
    }

    public void setSlotId(LongFilter slotId) {
        this.slotId = slotId;
    }

    public LongFilter getDriverId() {
        return driverId;
    }

    public Optional<LongFilter> optionalDriverId() {
        return Optional.ofNullable(driverId);
    }

    public LongFilter driverId() {
        if (driverId == null) {
            setDriverId(new LongFilter());
        }
        return driverId;
    }

    public void setDriverId(LongFilter driverId) {
        this.driverId = driverId;
    }

    public LongFilter getAttendantId() {
        return attendantId;
    }

    public Optional<LongFilter> optionalAttendantId() {
        return Optional.ofNullable(attendantId);
    }

    public LongFilter attendantId() {
        if (attendantId == null) {
            setAttendantId(new LongFilter());
        }
        return attendantId;
    }

    public void setAttendantId(LongFilter attendantId) {
        this.attendantId = attendantId;
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

    public VehicleTypeFilter getVehicleType() {
        return vehicleType();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TripCriteria that = (TripCriteria) o;
        return (Objects.equals(id, that.id) &&
                Objects.equals(tripCode, that.tripCode) &&
                Objects.equals(departureTime, that.departureTime) &&
                Objects.equals(arrivalTime, that.arrivalTime) &&
                Objects.equals(occasionFactor, that.occasionFactor) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(isDeleted, that.isDeleted) &&
                Objects.equals(deletedAt, that.deletedAt) &&
                Objects.equals(deletedBy, that.deletedBy) &&
                Objects.equals(routeId, that.routeId) &&
                Objects.equals(vehicleId, that.vehicleId) &&
                Objects.equals(slotId, that.slotId) &&
                Objects.equals(driverId, that.driverId) &&
                Objects.equals(attendantId, that.attendantId) &&
                Objects.equals(vehicleType, that.vehicleType) &&
                Objects.equals(destinationDistrictCode, that.destinationDistrictCode) &&
                Objects.equals(destinationProvinceCode, that.destinationProvinceCode) &&
                Objects.equals(vehiclePlateNumber, that.vehiclePlateNumber) &&
                Objects.equals(distinct, that.distinct));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                tripCode,
                departureTime,
                arrivalTime,
                occasionFactor,
                createdAt,
                updatedAt,
                isDeleted,
                deletedAt,
                deletedBy,
                routeId,
                vehicleId,
                slotId,
                driverId,
                attendantId,
                vehicleType,
                destinationDistrictCode,
                destinationProvinceCode,
                vehiclePlateNumber,
                distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripCriteria{" +
                optionalId().map(f -> "id=" + f + ", ").orElse("") +
                optionalTripCode().map(f -> "tripCode=" + f + ", ").orElse("") +
                optionalDepartureTime().map(f -> "departureTime=" + f + ", ").orElse("") +
                optionalArrivalTime().map(f -> "arrivalTime=" + f + ", ").orElse("") +
                optionalOccasionFactor().map(f -> "occasionFactor=" + f + ", ").orElse("") +
                optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
                optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
                optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
                optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
                optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
                optionalRouteId().map(f -> "routeId=" + f + ", ").orElse("") +
                optionalVehicleId().map(f -> "vehicleId=" + f + ", ").orElse("") +
                optionalSlotId().map(f -> "slotId=" + f + ", ").orElse("") +
                optionalDriverId().map(f -> "driverId=" + f + ", ").orElse("") +
                optionalAttendantId().map(f -> "attendantId=" + f + ", ").orElse("") +
                optionalVehicleType().map(f -> "vehicleType=" + f + ", ").orElse("") +
                optionalDestinationDistrictCode().map(f -> "destinationDistrictCode=" + f + ", ").orElse("") +
                optionalDestinationProvinceCode().map(f -> "destinationProvinceCode=" + f + ", ").orElse("") +
                optionalVehiclePlateNumber().map(f -> "vehiclePlateNumber=" + f + ", ").orElse("") +
                optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
                "}";
    }
}
