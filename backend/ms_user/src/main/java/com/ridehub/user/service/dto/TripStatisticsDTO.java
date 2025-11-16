package com.ridehub.user.service.dto;

import com.ridehub.user.domain.enumeration.OccasionType;
import com.ridehub.user.domain.enumeration.VehicleType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.user.domain.TripStatistics} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripStatisticsDTO implements Serializable {

    private Long id;

    @NotNull
    private Long routeId;

    @NotNull
    private VehicleType vehicleType;

    @NotNull
    private OccasionType occasionType;

    private Integer totalBookings;

    private BigDecimal totalRevenue;

    private BigDecimal averagePrice;

    private BigDecimal occupancyRate;

    @Size(max = 200)
    private String popularSeatTypes;

    @Size(max = 500)
    private String peakTravelTimes;

    private BigDecimal cancellationRate;

    private BigDecimal customerSatisfactionScore;

    @Size(max = 1000)
    private String monthlyTrend;

    @NotNull
    private LocalDate validFrom;

    private LocalDate validTo;

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

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public OccasionType getOccasionType() {
        return occasionType;
    }

    public void setOccasionType(OccasionType occasionType) {
        this.occasionType = occasionType;
    }

    public Integer getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Integer totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(BigDecimal occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public String getPopularSeatTypes() {
        return popularSeatTypes;
    }

    public void setPopularSeatTypes(String popularSeatTypes) {
        this.popularSeatTypes = popularSeatTypes;
    }

    public String getPeakTravelTimes() {
        return peakTravelTimes;
    }

    public void setPeakTravelTimes(String peakTravelTimes) {
        this.peakTravelTimes = peakTravelTimes;
    }

    public BigDecimal getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(BigDecimal cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public BigDecimal getCustomerSatisfactionScore() {
        return customerSatisfactionScore;
    }

    public void setCustomerSatisfactionScore(BigDecimal customerSatisfactionScore) {
        this.customerSatisfactionScore = customerSatisfactionScore;
    }

    public String getMonthlyTrend() {
        return monthlyTrend;
    }

    public void setMonthlyTrend(String monthlyTrend) {
        this.monthlyTrend = monthlyTrend;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
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
        if (!(o instanceof TripStatisticsDTO)) {
            return false;
        }

        TripStatisticsDTO tripStatisticsDTO = (TripStatisticsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tripStatisticsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripStatisticsDTO{" +
            "id=" + getId() +
            ", routeId=" + getRouteId() +
            ", vehicleType='" + getVehicleType() + "'" +
            ", occasionType='" + getOccasionType() + "'" +
            ", totalBookings=" + getTotalBookings() +
            ", totalRevenue=" + getTotalRevenue() +
            ", averagePrice=" + getAveragePrice() +
            ", occupancyRate=" + getOccupancyRate() +
            ", popularSeatTypes='" + getPopularSeatTypes() + "'" +
            ", peakTravelTimes='" + getPeakTravelTimes() + "'" +
            ", cancellationRate=" + getCancellationRate() +
            ", customerSatisfactionScore=" + getCustomerSatisfactionScore() +
            ", monthlyTrend='" + getMonthlyTrend() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
