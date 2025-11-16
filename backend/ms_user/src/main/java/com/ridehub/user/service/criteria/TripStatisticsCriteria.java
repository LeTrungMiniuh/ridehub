package com.ridehub.user.service.criteria;

import com.ridehub.user.domain.enumeration.OccasionType;
import com.ridehub.user.domain.enumeration.VehicleType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.user.domain.TripStatistics} entity. This class is used
 * in {@link com.ridehub.user.web.rest.TripStatisticsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trip-statistics?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripStatisticsCriteria implements Serializable, Criteria {

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

    private LongFilter routeId;

    private VehicleTypeFilter vehicleType;

    private OccasionTypeFilter occasionType;

    private IntegerFilter totalBookings;

    private BigDecimalFilter totalRevenue;

    private BigDecimalFilter averagePrice;

    private BigDecimalFilter occupancyRate;

    private StringFilter popularSeatTypes;

    private StringFilter peakTravelTimes;

    private BigDecimalFilter cancellationRate;

    private BigDecimalFilter customerSatisfactionScore;

    private StringFilter monthlyTrend;

    private LocalDateFilter validFrom;

    private LocalDateFilter validTo;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private Boolean distinct;

    public TripStatisticsCriteria() {}

    public TripStatisticsCriteria(TripStatisticsCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.routeId = other.optionalRouteId().map(LongFilter::copy).orElse(null);
        this.vehicleType = other.optionalVehicleType().map(VehicleTypeFilter::copy).orElse(null);
        this.occasionType = other.optionalOccasionType().map(OccasionTypeFilter::copy).orElse(null);
        this.totalBookings = other.optionalTotalBookings().map(IntegerFilter::copy).orElse(null);
        this.totalRevenue = other.optionalTotalRevenue().map(BigDecimalFilter::copy).orElse(null);
        this.averagePrice = other.optionalAveragePrice().map(BigDecimalFilter::copy).orElse(null);
        this.occupancyRate = other.optionalOccupancyRate().map(BigDecimalFilter::copy).orElse(null);
        this.popularSeatTypes = other.optionalPopularSeatTypes().map(StringFilter::copy).orElse(null);
        this.peakTravelTimes = other.optionalPeakTravelTimes().map(StringFilter::copy).orElse(null);
        this.cancellationRate = other.optionalCancellationRate().map(BigDecimalFilter::copy).orElse(null);
        this.customerSatisfactionScore = other.optionalCustomerSatisfactionScore().map(BigDecimalFilter::copy).orElse(null);
        this.monthlyTrend = other.optionalMonthlyTrend().map(StringFilter::copy).orElse(null);
        this.validFrom = other.optionalValidFrom().map(LocalDateFilter::copy).orElse(null);
        this.validTo = other.optionalValidTo().map(LocalDateFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TripStatisticsCriteria copy() {
        return new TripStatisticsCriteria(this);
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

    public IntegerFilter getTotalBookings() {
        return totalBookings;
    }

    public Optional<IntegerFilter> optionalTotalBookings() {
        return Optional.ofNullable(totalBookings);
    }

    public IntegerFilter totalBookings() {
        if (totalBookings == null) {
            setTotalBookings(new IntegerFilter());
        }
        return totalBookings;
    }

    public void setTotalBookings(IntegerFilter totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimalFilter getTotalRevenue() {
        return totalRevenue;
    }

    public Optional<BigDecimalFilter> optionalTotalRevenue() {
        return Optional.ofNullable(totalRevenue);
    }

    public BigDecimalFilter totalRevenue() {
        if (totalRevenue == null) {
            setTotalRevenue(new BigDecimalFilter());
        }
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimalFilter totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimalFilter getAveragePrice() {
        return averagePrice;
    }

    public Optional<BigDecimalFilter> optionalAveragePrice() {
        return Optional.ofNullable(averagePrice);
    }

    public BigDecimalFilter averagePrice() {
        if (averagePrice == null) {
            setAveragePrice(new BigDecimalFilter());
        }
        return averagePrice;
    }

    public void setAveragePrice(BigDecimalFilter averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimalFilter getOccupancyRate() {
        return occupancyRate;
    }

    public Optional<BigDecimalFilter> optionalOccupancyRate() {
        return Optional.ofNullable(occupancyRate);
    }

    public BigDecimalFilter occupancyRate() {
        if (occupancyRate == null) {
            setOccupancyRate(new BigDecimalFilter());
        }
        return occupancyRate;
    }

    public void setOccupancyRate(BigDecimalFilter occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public StringFilter getPopularSeatTypes() {
        return popularSeatTypes;
    }

    public Optional<StringFilter> optionalPopularSeatTypes() {
        return Optional.ofNullable(popularSeatTypes);
    }

    public StringFilter popularSeatTypes() {
        if (popularSeatTypes == null) {
            setPopularSeatTypes(new StringFilter());
        }
        return popularSeatTypes;
    }

    public void setPopularSeatTypes(StringFilter popularSeatTypes) {
        this.popularSeatTypes = popularSeatTypes;
    }

    public StringFilter getPeakTravelTimes() {
        return peakTravelTimes;
    }

    public Optional<StringFilter> optionalPeakTravelTimes() {
        return Optional.ofNullable(peakTravelTimes);
    }

    public StringFilter peakTravelTimes() {
        if (peakTravelTimes == null) {
            setPeakTravelTimes(new StringFilter());
        }
        return peakTravelTimes;
    }

    public void setPeakTravelTimes(StringFilter peakTravelTimes) {
        this.peakTravelTimes = peakTravelTimes;
    }

    public BigDecimalFilter getCancellationRate() {
        return cancellationRate;
    }

    public Optional<BigDecimalFilter> optionalCancellationRate() {
        return Optional.ofNullable(cancellationRate);
    }

    public BigDecimalFilter cancellationRate() {
        if (cancellationRate == null) {
            setCancellationRate(new BigDecimalFilter());
        }
        return cancellationRate;
    }

    public void setCancellationRate(BigDecimalFilter cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public BigDecimalFilter getCustomerSatisfactionScore() {
        return customerSatisfactionScore;
    }

    public Optional<BigDecimalFilter> optionalCustomerSatisfactionScore() {
        return Optional.ofNullable(customerSatisfactionScore);
    }

    public BigDecimalFilter customerSatisfactionScore() {
        if (customerSatisfactionScore == null) {
            setCustomerSatisfactionScore(new BigDecimalFilter());
        }
        return customerSatisfactionScore;
    }

    public void setCustomerSatisfactionScore(BigDecimalFilter customerSatisfactionScore) {
        this.customerSatisfactionScore = customerSatisfactionScore;
    }

    public StringFilter getMonthlyTrend() {
        return monthlyTrend;
    }

    public Optional<StringFilter> optionalMonthlyTrend() {
        return Optional.ofNullable(monthlyTrend);
    }

    public StringFilter monthlyTrend() {
        if (monthlyTrend == null) {
            setMonthlyTrend(new StringFilter());
        }
        return monthlyTrend;
    }

    public void setMonthlyTrend(StringFilter monthlyTrend) {
        this.monthlyTrend = monthlyTrend;
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
        final TripStatisticsCriteria that = (TripStatisticsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(routeId, that.routeId) &&
            Objects.equals(vehicleType, that.vehicleType) &&
            Objects.equals(occasionType, that.occasionType) &&
            Objects.equals(totalBookings, that.totalBookings) &&
            Objects.equals(totalRevenue, that.totalRevenue) &&
            Objects.equals(averagePrice, that.averagePrice) &&
            Objects.equals(occupancyRate, that.occupancyRate) &&
            Objects.equals(popularSeatTypes, that.popularSeatTypes) &&
            Objects.equals(peakTravelTimes, that.peakTravelTimes) &&
            Objects.equals(cancellationRate, that.cancellationRate) &&
            Objects.equals(customerSatisfactionScore, that.customerSatisfactionScore) &&
            Objects.equals(monthlyTrend, that.monthlyTrend) &&
            Objects.equals(validFrom, that.validFrom) &&
            Objects.equals(validTo, that.validTo) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            routeId,
            vehicleType,
            occasionType,
            totalBookings,
            totalRevenue,
            averagePrice,
            occupancyRate,
            popularSeatTypes,
            peakTravelTimes,
            cancellationRate,
            customerSatisfactionScore,
            monthlyTrend,
            validFrom,
            validTo,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripStatisticsCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRouteId().map(f -> "routeId=" + f + ", ").orElse("") +
            optionalVehicleType().map(f -> "vehicleType=" + f + ", ").orElse("") +
            optionalOccasionType().map(f -> "occasionType=" + f + ", ").orElse("") +
            optionalTotalBookings().map(f -> "totalBookings=" + f + ", ").orElse("") +
            optionalTotalRevenue().map(f -> "totalRevenue=" + f + ", ").orElse("") +
            optionalAveragePrice().map(f -> "averagePrice=" + f + ", ").orElse("") +
            optionalOccupancyRate().map(f -> "occupancyRate=" + f + ", ").orElse("") +
            optionalPopularSeatTypes().map(f -> "popularSeatTypes=" + f + ", ").orElse("") +
            optionalPeakTravelTimes().map(f -> "peakTravelTimes=" + f + ", ").orElse("") +
            optionalCancellationRate().map(f -> "cancellationRate=" + f + ", ").orElse("") +
            optionalCustomerSatisfactionScore().map(f -> "customerSatisfactionScore=" + f + ", ").orElse("") +
            optionalMonthlyTrend().map(f -> "monthlyTrend=" + f + ", ").orElse("") +
            optionalValidFrom().map(f -> "validFrom=" + f + ", ").orElse("") +
            optionalValidTo().map(f -> "validTo=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
