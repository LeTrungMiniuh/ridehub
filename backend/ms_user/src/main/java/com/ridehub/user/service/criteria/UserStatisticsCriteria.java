package com.ridehub.user.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.user.domain.UserStatistics} entity. This class is used
 * in {@link com.ridehub.user.web.rest.UserStatisticsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-statistics?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserStatisticsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter totalTrips;

    private BigDecimalFilter totalSpent;

    private StringFilter favoriteRoutes;

    private StringFilter preferredVehicleTypes;

    private IntegerFilter averageTripDuration;

    private LocalDateFilter lastTravelDate;

    private StringFilter bookingFrequency;

    private IntegerFilter loyaltyPoints;

    private StringFilter mostFrequentOrigin;

    private StringFilter mostFrequentDestination;

    private BigDecimalFilter averageTripDistance;

    private StringFilter peakTravelTime;

    private IntegerFilter weekendTrips;

    private IntegerFilter holidayTrips;

    private IntegerFilter cancelledTrips;

    private BigDecimalFilter onTimePerformanceRate;

    private StringFilter preferredSeatTypes;

    private IntegerFilter monthlyTripCount;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter userId;

    private Boolean distinct;

    public UserStatisticsCriteria() {}

    public UserStatisticsCriteria(UserStatisticsCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.totalTrips = other.optionalTotalTrips().map(IntegerFilter::copy).orElse(null);
        this.totalSpent = other.optionalTotalSpent().map(BigDecimalFilter::copy).orElse(null);
        this.favoriteRoutes = other.optionalFavoriteRoutes().map(StringFilter::copy).orElse(null);
        this.preferredVehicleTypes = other.optionalPreferredVehicleTypes().map(StringFilter::copy).orElse(null);
        this.averageTripDuration = other.optionalAverageTripDuration().map(IntegerFilter::copy).orElse(null);
        this.lastTravelDate = other.optionalLastTravelDate().map(LocalDateFilter::copy).orElse(null);
        this.bookingFrequency = other.optionalBookingFrequency().map(StringFilter::copy).orElse(null);
        this.loyaltyPoints = other.optionalLoyaltyPoints().map(IntegerFilter::copy).orElse(null);
        this.mostFrequentOrigin = other.optionalMostFrequentOrigin().map(StringFilter::copy).orElse(null);
        this.mostFrequentDestination = other.optionalMostFrequentDestination().map(StringFilter::copy).orElse(null);
        this.averageTripDistance = other.optionalAverageTripDistance().map(BigDecimalFilter::copy).orElse(null);
        this.peakTravelTime = other.optionalPeakTravelTime().map(StringFilter::copy).orElse(null);
        this.weekendTrips = other.optionalWeekendTrips().map(IntegerFilter::copy).orElse(null);
        this.holidayTrips = other.optionalHolidayTrips().map(IntegerFilter::copy).orElse(null);
        this.cancelledTrips = other.optionalCancelledTrips().map(IntegerFilter::copy).orElse(null);
        this.onTimePerformanceRate = other.optionalOnTimePerformanceRate().map(BigDecimalFilter::copy).orElse(null);
        this.preferredSeatTypes = other.optionalPreferredSeatTypes().map(StringFilter::copy).orElse(null);
        this.monthlyTripCount = other.optionalMonthlyTripCount().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UserStatisticsCriteria copy() {
        return new UserStatisticsCriteria(this);
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

    public IntegerFilter getTotalTrips() {
        return totalTrips;
    }

    public Optional<IntegerFilter> optionalTotalTrips() {
        return Optional.ofNullable(totalTrips);
    }

    public IntegerFilter totalTrips() {
        if (totalTrips == null) {
            setTotalTrips(new IntegerFilter());
        }
        return totalTrips;
    }

    public void setTotalTrips(IntegerFilter totalTrips) {
        this.totalTrips = totalTrips;
    }

    public BigDecimalFilter getTotalSpent() {
        return totalSpent;
    }

    public Optional<BigDecimalFilter> optionalTotalSpent() {
        return Optional.ofNullable(totalSpent);
    }

    public BigDecimalFilter totalSpent() {
        if (totalSpent == null) {
            setTotalSpent(new BigDecimalFilter());
        }
        return totalSpent;
    }

    public void setTotalSpent(BigDecimalFilter totalSpent) {
        this.totalSpent = totalSpent;
    }

    public StringFilter getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public Optional<StringFilter> optionalFavoriteRoutes() {
        return Optional.ofNullable(favoriteRoutes);
    }

    public StringFilter favoriteRoutes() {
        if (favoriteRoutes == null) {
            setFavoriteRoutes(new StringFilter());
        }
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(StringFilter favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    public StringFilter getPreferredVehicleTypes() {
        return preferredVehicleTypes;
    }

    public Optional<StringFilter> optionalPreferredVehicleTypes() {
        return Optional.ofNullable(preferredVehicleTypes);
    }

    public StringFilter preferredVehicleTypes() {
        if (preferredVehicleTypes == null) {
            setPreferredVehicleTypes(new StringFilter());
        }
        return preferredVehicleTypes;
    }

    public void setPreferredVehicleTypes(StringFilter preferredVehicleTypes) {
        this.preferredVehicleTypes = preferredVehicleTypes;
    }

    public IntegerFilter getAverageTripDuration() {
        return averageTripDuration;
    }

    public Optional<IntegerFilter> optionalAverageTripDuration() {
        return Optional.ofNullable(averageTripDuration);
    }

    public IntegerFilter averageTripDuration() {
        if (averageTripDuration == null) {
            setAverageTripDuration(new IntegerFilter());
        }
        return averageTripDuration;
    }

    public void setAverageTripDuration(IntegerFilter averageTripDuration) {
        this.averageTripDuration = averageTripDuration;
    }

    public LocalDateFilter getLastTravelDate() {
        return lastTravelDate;
    }

    public Optional<LocalDateFilter> optionalLastTravelDate() {
        return Optional.ofNullable(lastTravelDate);
    }

    public LocalDateFilter lastTravelDate() {
        if (lastTravelDate == null) {
            setLastTravelDate(new LocalDateFilter());
        }
        return lastTravelDate;
    }

    public void setLastTravelDate(LocalDateFilter lastTravelDate) {
        this.lastTravelDate = lastTravelDate;
    }

    public StringFilter getBookingFrequency() {
        return bookingFrequency;
    }

    public Optional<StringFilter> optionalBookingFrequency() {
        return Optional.ofNullable(bookingFrequency);
    }

    public StringFilter bookingFrequency() {
        if (bookingFrequency == null) {
            setBookingFrequency(new StringFilter());
        }
        return bookingFrequency;
    }

    public void setBookingFrequency(StringFilter bookingFrequency) {
        this.bookingFrequency = bookingFrequency;
    }

    public IntegerFilter getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Optional<IntegerFilter> optionalLoyaltyPoints() {
        return Optional.ofNullable(loyaltyPoints);
    }

    public IntegerFilter loyaltyPoints() {
        if (loyaltyPoints == null) {
            setLoyaltyPoints(new IntegerFilter());
        }
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(IntegerFilter loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public StringFilter getMostFrequentOrigin() {
        return mostFrequentOrigin;
    }

    public Optional<StringFilter> optionalMostFrequentOrigin() {
        return Optional.ofNullable(mostFrequentOrigin);
    }

    public StringFilter mostFrequentOrigin() {
        if (mostFrequentOrigin == null) {
            setMostFrequentOrigin(new StringFilter());
        }
        return mostFrequentOrigin;
    }

    public void setMostFrequentOrigin(StringFilter mostFrequentOrigin) {
        this.mostFrequentOrigin = mostFrequentOrigin;
    }

    public StringFilter getMostFrequentDestination() {
        return mostFrequentDestination;
    }

    public Optional<StringFilter> optionalMostFrequentDestination() {
        return Optional.ofNullable(mostFrequentDestination);
    }

    public StringFilter mostFrequentDestination() {
        if (mostFrequentDestination == null) {
            setMostFrequentDestination(new StringFilter());
        }
        return mostFrequentDestination;
    }

    public void setMostFrequentDestination(StringFilter mostFrequentDestination) {
        this.mostFrequentDestination = mostFrequentDestination;
    }

    public BigDecimalFilter getAverageTripDistance() {
        return averageTripDistance;
    }

    public Optional<BigDecimalFilter> optionalAverageTripDistance() {
        return Optional.ofNullable(averageTripDistance);
    }

    public BigDecimalFilter averageTripDistance() {
        if (averageTripDistance == null) {
            setAverageTripDistance(new BigDecimalFilter());
        }
        return averageTripDistance;
    }

    public void setAverageTripDistance(BigDecimalFilter averageTripDistance) {
        this.averageTripDistance = averageTripDistance;
    }

    public StringFilter getPeakTravelTime() {
        return peakTravelTime;
    }

    public Optional<StringFilter> optionalPeakTravelTime() {
        return Optional.ofNullable(peakTravelTime);
    }

    public StringFilter peakTravelTime() {
        if (peakTravelTime == null) {
            setPeakTravelTime(new StringFilter());
        }
        return peakTravelTime;
    }

    public void setPeakTravelTime(StringFilter peakTravelTime) {
        this.peakTravelTime = peakTravelTime;
    }

    public IntegerFilter getWeekendTrips() {
        return weekendTrips;
    }

    public Optional<IntegerFilter> optionalWeekendTrips() {
        return Optional.ofNullable(weekendTrips);
    }

    public IntegerFilter weekendTrips() {
        if (weekendTrips == null) {
            setWeekendTrips(new IntegerFilter());
        }
        return weekendTrips;
    }

    public void setWeekendTrips(IntegerFilter weekendTrips) {
        this.weekendTrips = weekendTrips;
    }

    public IntegerFilter getHolidayTrips() {
        return holidayTrips;
    }

    public Optional<IntegerFilter> optionalHolidayTrips() {
        return Optional.ofNullable(holidayTrips);
    }

    public IntegerFilter holidayTrips() {
        if (holidayTrips == null) {
            setHolidayTrips(new IntegerFilter());
        }
        return holidayTrips;
    }

    public void setHolidayTrips(IntegerFilter holidayTrips) {
        this.holidayTrips = holidayTrips;
    }

    public IntegerFilter getCancelledTrips() {
        return cancelledTrips;
    }

    public Optional<IntegerFilter> optionalCancelledTrips() {
        return Optional.ofNullable(cancelledTrips);
    }

    public IntegerFilter cancelledTrips() {
        if (cancelledTrips == null) {
            setCancelledTrips(new IntegerFilter());
        }
        return cancelledTrips;
    }

    public void setCancelledTrips(IntegerFilter cancelledTrips) {
        this.cancelledTrips = cancelledTrips;
    }

    public BigDecimalFilter getOnTimePerformanceRate() {
        return onTimePerformanceRate;
    }

    public Optional<BigDecimalFilter> optionalOnTimePerformanceRate() {
        return Optional.ofNullable(onTimePerformanceRate);
    }

    public BigDecimalFilter onTimePerformanceRate() {
        if (onTimePerformanceRate == null) {
            setOnTimePerformanceRate(new BigDecimalFilter());
        }
        return onTimePerformanceRate;
    }

    public void setOnTimePerformanceRate(BigDecimalFilter onTimePerformanceRate) {
        this.onTimePerformanceRate = onTimePerformanceRate;
    }

    public StringFilter getPreferredSeatTypes() {
        return preferredSeatTypes;
    }

    public Optional<StringFilter> optionalPreferredSeatTypes() {
        return Optional.ofNullable(preferredSeatTypes);
    }

    public StringFilter preferredSeatTypes() {
        if (preferredSeatTypes == null) {
            setPreferredSeatTypes(new StringFilter());
        }
        return preferredSeatTypes;
    }

    public void setPreferredSeatTypes(StringFilter preferredSeatTypes) {
        this.preferredSeatTypes = preferredSeatTypes;
    }

    public IntegerFilter getMonthlyTripCount() {
        return monthlyTripCount;
    }

    public Optional<IntegerFilter> optionalMonthlyTripCount() {
        return Optional.ofNullable(monthlyTripCount);
    }

    public IntegerFilter monthlyTripCount() {
        if (monthlyTripCount == null) {
            setMonthlyTripCount(new IntegerFilter());
        }
        return monthlyTripCount;
    }

    public void setMonthlyTripCount(IntegerFilter monthlyTripCount) {
        this.monthlyTripCount = monthlyTripCount;
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

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final UserStatisticsCriteria that = (UserStatisticsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(totalTrips, that.totalTrips) &&
            Objects.equals(totalSpent, that.totalSpent) &&
            Objects.equals(favoriteRoutes, that.favoriteRoutes) &&
            Objects.equals(preferredVehicleTypes, that.preferredVehicleTypes) &&
            Objects.equals(averageTripDuration, that.averageTripDuration) &&
            Objects.equals(lastTravelDate, that.lastTravelDate) &&
            Objects.equals(bookingFrequency, that.bookingFrequency) &&
            Objects.equals(loyaltyPoints, that.loyaltyPoints) &&
            Objects.equals(mostFrequentOrigin, that.mostFrequentOrigin) &&
            Objects.equals(mostFrequentDestination, that.mostFrequentDestination) &&
            Objects.equals(averageTripDistance, that.averageTripDistance) &&
            Objects.equals(peakTravelTime, that.peakTravelTime) &&
            Objects.equals(weekendTrips, that.weekendTrips) &&
            Objects.equals(holidayTrips, that.holidayTrips) &&
            Objects.equals(cancelledTrips, that.cancelledTrips) &&
            Objects.equals(onTimePerformanceRate, that.onTimePerformanceRate) &&
            Objects.equals(preferredSeatTypes, that.preferredSeatTypes) &&
            Objects.equals(monthlyTripCount, that.monthlyTripCount) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            totalTrips,
            totalSpent,
            favoriteRoutes,
            preferredVehicleTypes,
            averageTripDuration,
            lastTravelDate,
            bookingFrequency,
            loyaltyPoints,
            mostFrequentOrigin,
            mostFrequentDestination,
            averageTripDistance,
            peakTravelTime,
            weekendTrips,
            holidayTrips,
            cancelledTrips,
            onTimePerformanceRate,
            preferredSeatTypes,
            monthlyTripCount,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserStatisticsCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTotalTrips().map(f -> "totalTrips=" + f + ", ").orElse("") +
            optionalTotalSpent().map(f -> "totalSpent=" + f + ", ").orElse("") +
            optionalFavoriteRoutes().map(f -> "favoriteRoutes=" + f + ", ").orElse("") +
            optionalPreferredVehicleTypes().map(f -> "preferredVehicleTypes=" + f + ", ").orElse("") +
            optionalAverageTripDuration().map(f -> "averageTripDuration=" + f + ", ").orElse("") +
            optionalLastTravelDate().map(f -> "lastTravelDate=" + f + ", ").orElse("") +
            optionalBookingFrequency().map(f -> "bookingFrequency=" + f + ", ").orElse("") +
            optionalLoyaltyPoints().map(f -> "loyaltyPoints=" + f + ", ").orElse("") +
            optionalMostFrequentOrigin().map(f -> "mostFrequentOrigin=" + f + ", ").orElse("") +
            optionalMostFrequentDestination().map(f -> "mostFrequentDestination=" + f + ", ").orElse("") +
            optionalAverageTripDistance().map(f -> "averageTripDistance=" + f + ", ").orElse("") +
            optionalPeakTravelTime().map(f -> "peakTravelTime=" + f + ", ").orElse("") +
            optionalWeekendTrips().map(f -> "weekendTrips=" + f + ", ").orElse("") +
            optionalHolidayTrips().map(f -> "holidayTrips=" + f + ", ").orElse("") +
            optionalCancelledTrips().map(f -> "cancelledTrips=" + f + ", ").orElse("") +
            optionalOnTimePerformanceRate().map(f -> "onTimePerformanceRate=" + f + ", ").orElse("") +
            optionalPreferredSeatTypes().map(f -> "preferredSeatTypes=" + f + ", ").orElse("") +
            optionalMonthlyTripCount().map(f -> "monthlyTripCount=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
