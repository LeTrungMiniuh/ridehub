package com.ridehub.user.domain;

import com.ridehub.user.domain.enumeration.OccasionType;
import com.ridehub.user.domain.enumeration.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A TripStatistics.
 */
@Entity
@Table(name = "trip_statistics")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "occasion_type", nullable = false)
    private OccasionType occasionType;

    @Column(name = "total_bookings")
    private Integer totalBookings;

    @Column(name = "total_revenue", precision = 21, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "average_price", precision = 21, scale = 2)
    private BigDecimal averagePrice;

    @Column(name = "occupancy_rate", precision = 21, scale = 2)
    private BigDecimal occupancyRate;

    @Size(max = 200)
    @Column(name = "popular_seat_types", length = 200)
    private String popularSeatTypes;

    @Size(max = 500)
    @Column(name = "peak_travel_times", length = 500)
    private String peakTravelTimes;

    @Column(name = "cancellation_rate", precision = 21, scale = 2)
    private BigDecimal cancellationRate;

    @Column(name = "customer_satisfaction_score", precision = 21, scale = 2)
    private BigDecimal customerSatisfactionScore;

    @Size(max = 1000)
    @Column(name = "monthly_trend", length = 1000)
    private String monthlyTrend;

    @NotNull
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TripStatistics id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return this.routeId;
    }

    public TripStatistics routeId(Long routeId) {
        this.setRouteId(routeId);
        return this;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public TripStatistics vehicleType(VehicleType vehicleType) {
        this.setVehicleType(vehicleType);
        return this;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public OccasionType getOccasionType() {
        return this.occasionType;
    }

    public TripStatistics occasionType(OccasionType occasionType) {
        this.setOccasionType(occasionType);
        return this;
    }

    public void setOccasionType(OccasionType occasionType) {
        this.occasionType = occasionType;
    }

    public Integer getTotalBookings() {
        return this.totalBookings;
    }

    public TripStatistics totalBookings(Integer totalBookings) {
        this.setTotalBookings(totalBookings);
        return this;
    }

    public void setTotalBookings(Integer totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getTotalRevenue() {
        return this.totalRevenue;
    }

    public TripStatistics totalRevenue(BigDecimal totalRevenue) {
        this.setTotalRevenue(totalRevenue);
        return this;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAveragePrice() {
        return this.averagePrice;
    }

    public TripStatistics averagePrice(BigDecimal averagePrice) {
        this.setAveragePrice(averagePrice);
        return this;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getOccupancyRate() {
        return this.occupancyRate;
    }

    public TripStatistics occupancyRate(BigDecimal occupancyRate) {
        this.setOccupancyRate(occupancyRate);
        return this;
    }

    public void setOccupancyRate(BigDecimal occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public String getPopularSeatTypes() {
        return this.popularSeatTypes;
    }

    public TripStatistics popularSeatTypes(String popularSeatTypes) {
        this.setPopularSeatTypes(popularSeatTypes);
        return this;
    }

    public void setPopularSeatTypes(String popularSeatTypes) {
        this.popularSeatTypes = popularSeatTypes;
    }

    public String getPeakTravelTimes() {
        return this.peakTravelTimes;
    }

    public TripStatistics peakTravelTimes(String peakTravelTimes) {
        this.setPeakTravelTimes(peakTravelTimes);
        return this;
    }

    public void setPeakTravelTimes(String peakTravelTimes) {
        this.peakTravelTimes = peakTravelTimes;
    }

    public BigDecimal getCancellationRate() {
        return this.cancellationRate;
    }

    public TripStatistics cancellationRate(BigDecimal cancellationRate) {
        this.setCancellationRate(cancellationRate);
        return this;
    }

    public void setCancellationRate(BigDecimal cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public BigDecimal getCustomerSatisfactionScore() {
        return this.customerSatisfactionScore;
    }

    public TripStatistics customerSatisfactionScore(BigDecimal customerSatisfactionScore) {
        this.setCustomerSatisfactionScore(customerSatisfactionScore);
        return this;
    }

    public void setCustomerSatisfactionScore(BigDecimal customerSatisfactionScore) {
        this.customerSatisfactionScore = customerSatisfactionScore;
    }

    public String getMonthlyTrend() {
        return this.monthlyTrend;
    }

    public TripStatistics monthlyTrend(String monthlyTrend) {
        this.setMonthlyTrend(monthlyTrend);
        return this;
    }

    public void setMonthlyTrend(String monthlyTrend) {
        this.monthlyTrend = monthlyTrend;
    }

    public LocalDate getValidFrom() {
        return this.validFrom;
    }

    public TripStatistics validFrom(LocalDate validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return this.validTo;
    }

    public TripStatistics validTo(LocalDate validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public TripStatistics createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public TripStatistics updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public TripStatistics isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public TripStatistics deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public TripStatistics deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TripStatistics)) {
            return false;
        }
        return getId() != null && getId().equals(((TripStatistics) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripStatistics{" +
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
