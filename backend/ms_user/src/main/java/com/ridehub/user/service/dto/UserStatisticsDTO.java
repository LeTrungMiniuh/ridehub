package com.ridehub.user.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.user.domain.UserStatistics} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserStatisticsDTO implements Serializable {

    private Long id;

    private Integer totalTrips;

    private BigDecimal totalSpent;

    @Size(max = 1000)
    private String favoriteRoutes;

    @Size(max = 200)
    private String preferredVehicleTypes;

    private Integer averageTripDuration;

    private LocalDate lastTravelDate;

    @Size(max = 50)
    private String bookingFrequency;

    private Integer loyaltyPoints;

    @Size(max = 200)
    private String mostFrequentOrigin;

    @Size(max = 200)
    private String mostFrequentDestination;

    private BigDecimal averageTripDistance;

    @Size(max = 50)
    private String peakTravelTime;

    private Integer weekendTrips;

    private Integer holidayTrips;

    private Integer cancelledTrips;

    private BigDecimal onTimePerformanceRate;

    @Size(max = 200)
    private String preferredSeatTypes;

    private Integer monthlyTripCount;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(Integer totalTrips) {
        this.totalTrips = totalTrips;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(String favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    public String getPreferredVehicleTypes() {
        return preferredVehicleTypes;
    }

    public void setPreferredVehicleTypes(String preferredVehicleTypes) {
        this.preferredVehicleTypes = preferredVehicleTypes;
    }

    public Integer getAverageTripDuration() {
        return averageTripDuration;
    }

    public void setAverageTripDuration(Integer averageTripDuration) {
        this.averageTripDuration = averageTripDuration;
    }

    public LocalDate getLastTravelDate() {
        return lastTravelDate;
    }

    public void setLastTravelDate(LocalDate lastTravelDate) {
        this.lastTravelDate = lastTravelDate;
    }

    public String getBookingFrequency() {
        return bookingFrequency;
    }

    public void setBookingFrequency(String bookingFrequency) {
        this.bookingFrequency = bookingFrequency;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMostFrequentOrigin() {
        return mostFrequentOrigin;
    }

    public void setMostFrequentOrigin(String mostFrequentOrigin) {
        this.mostFrequentOrigin = mostFrequentOrigin;
    }

    public String getMostFrequentDestination() {
        return mostFrequentDestination;
    }

    public void setMostFrequentDestination(String mostFrequentDestination) {
        this.mostFrequentDestination = mostFrequentDestination;
    }

    public BigDecimal getAverageTripDistance() {
        return averageTripDistance;
    }

    public void setAverageTripDistance(BigDecimal averageTripDistance) {
        this.averageTripDistance = averageTripDistance;
    }

    public String getPeakTravelTime() {
        return peakTravelTime;
    }

    public void setPeakTravelTime(String peakTravelTime) {
        this.peakTravelTime = peakTravelTime;
    }

    public Integer getWeekendTrips() {
        return weekendTrips;
    }

    public void setWeekendTrips(Integer weekendTrips) {
        this.weekendTrips = weekendTrips;
    }

    public Integer getHolidayTrips() {
        return holidayTrips;
    }

    public void setHolidayTrips(Integer holidayTrips) {
        this.holidayTrips = holidayTrips;
    }

    public Integer getCancelledTrips() {
        return cancelledTrips;
    }

    public void setCancelledTrips(Integer cancelledTrips) {
        this.cancelledTrips = cancelledTrips;
    }

    public BigDecimal getOnTimePerformanceRate() {
        return onTimePerformanceRate;
    }

    public void setOnTimePerformanceRate(BigDecimal onTimePerformanceRate) {
        this.onTimePerformanceRate = onTimePerformanceRate;
    }

    public String getPreferredSeatTypes() {
        return preferredSeatTypes;
    }

    public void setPreferredSeatTypes(String preferredSeatTypes) {
        this.preferredSeatTypes = preferredSeatTypes;
    }

    public Integer getMonthlyTripCount() {
        return monthlyTripCount;
    }

    public void setMonthlyTripCount(Integer monthlyTripCount) {
        this.monthlyTripCount = monthlyTripCount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserStatisticsDTO)) {
            return false;
        }

        UserStatisticsDTO userStatisticsDTO = (UserStatisticsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userStatisticsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserStatisticsDTO{" +
            "id=" + getId() +
            ", totalTrips=" + getTotalTrips() +
            ", totalSpent=" + getTotalSpent() +
            ", favoriteRoutes='" + getFavoriteRoutes() + "'" +
            ", preferredVehicleTypes='" + getPreferredVehicleTypes() + "'" +
            ", averageTripDuration=" + getAverageTripDuration() +
            ", lastTravelDate='" + getLastTravelDate() + "'" +
            ", bookingFrequency='" + getBookingFrequency() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            ", mostFrequentOrigin='" + getMostFrequentOrigin() + "'" +
            ", mostFrequentDestination='" + getMostFrequentDestination() + "'" +
            ", averageTripDistance=" + getAverageTripDistance() +
            ", peakTravelTime='" + getPeakTravelTime() + "'" +
            ", weekendTrips=" + getWeekendTrips() +
            ", holidayTrips=" + getHolidayTrips() +
            ", cancelledTrips=" + getCancelledTrips() +
            ", onTimePerformanceRate=" + getOnTimePerformanceRate() +
            ", preferredSeatTypes='" + getPreferredSeatTypes() + "'" +
            ", monthlyTripCount=" + getMonthlyTripCount() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
