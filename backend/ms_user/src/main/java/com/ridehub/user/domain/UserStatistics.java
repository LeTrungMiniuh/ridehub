package com.ridehub.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * A UserStatistics.
 */
@Entity
@Table(name = "user_statistics")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_trips")
    private Integer totalTrips;

    @Column(name = "total_spent", precision = 21, scale = 2)
    private BigDecimal totalSpent;

    @Size(max = 1000)
    @Column(name = "favorite_routes", length = 1000)
    private String favoriteRoutes;

    @Size(max = 200)
    @Column(name = "preferred_vehicle_types", length = 200)
    private String preferredVehicleTypes;

    @Column(name = "average_trip_duration")
    private Integer averageTripDuration;

    @Column(name = "last_travel_date")
    private LocalDate lastTravelDate;

    @Size(max = 50)
    @Column(name = "booking_frequency", length = 50)
    private String bookingFrequency;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;

    @Size(max = 200)
    @Column(name = "most_frequent_origin", length = 200)
    private String mostFrequentOrigin;

    @Size(max = 200)
    @Column(name = "most_frequent_destination", length = 200)
    private String mostFrequentDestination;

    @Column(name = "average_trip_distance", precision = 21, scale = 2)
    private BigDecimal averageTripDistance;

    @Size(max = 50)
    @Column(name = "peak_travel_time", length = 50)
    private String peakTravelTime;

    @Column(name = "weekend_trips")
    private Integer weekendTrips;

    @Column(name = "holiday_trips")
    private Integer holidayTrips;

    @Column(name = "cancelled_trips")
    private Integer cancelledTrips;

    @Column(name = "on_time_performance_rate", precision = 21, scale = 2)
    private BigDecimal onTimePerformanceRate;

    @Size(max = 200)
    @Column(name = "preferred_seat_types", length = 200)
    private String preferredSeatTypes;

    @Column(name = "monthly_trip_count")
    private Integer monthlyTripCount;

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

    @JsonIgnoreProperties(value = { "profile", "statistics", "chatSessions", "recommendations" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "statistics")
    private AppUser user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserStatistics id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalTrips() {
        return this.totalTrips;
    }

    public UserStatistics totalTrips(Integer totalTrips) {
        this.setTotalTrips(totalTrips);
        return this;
    }

    public void setTotalTrips(Integer totalTrips) {
        this.totalTrips = totalTrips;
    }

    public BigDecimal getTotalSpent() {
        return this.totalSpent;
    }

    public UserStatistics totalSpent(BigDecimal totalSpent) {
        this.setTotalSpent(totalSpent);
        return this;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getFavoriteRoutes() {
        return this.favoriteRoutes;
    }

    public UserStatistics favoriteRoutes(String favoriteRoutes) {
        this.setFavoriteRoutes(favoriteRoutes);
        return this;
    }

    public void setFavoriteRoutes(String favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    public String getPreferredVehicleTypes() {
        return this.preferredVehicleTypes;
    }

    public UserStatistics preferredVehicleTypes(String preferredVehicleTypes) {
        this.setPreferredVehicleTypes(preferredVehicleTypes);
        return this;
    }

    public void setPreferredVehicleTypes(String preferredVehicleTypes) {
        this.preferredVehicleTypes = preferredVehicleTypes;
    }

    public Integer getAverageTripDuration() {
        return this.averageTripDuration;
    }

    public UserStatistics averageTripDuration(Integer averageTripDuration) {
        this.setAverageTripDuration(averageTripDuration);
        return this;
    }

    public void setAverageTripDuration(Integer averageTripDuration) {
        this.averageTripDuration = averageTripDuration;
    }

    public LocalDate getLastTravelDate() {
        return this.lastTravelDate;
    }

    public UserStatistics lastTravelDate(LocalDate lastTravelDate) {
        this.setLastTravelDate(lastTravelDate);
        return this;
    }

    public void setLastTravelDate(LocalDate lastTravelDate) {
        this.lastTravelDate = lastTravelDate;
    }

    public String getBookingFrequency() {
        return this.bookingFrequency;
    }

    public UserStatistics bookingFrequency(String bookingFrequency) {
        this.setBookingFrequency(bookingFrequency);
        return this;
    }

    public void setBookingFrequency(String bookingFrequency) {
        this.bookingFrequency = bookingFrequency;
    }

    public Integer getLoyaltyPoints() {
        return this.loyaltyPoints;
    }

    public UserStatistics loyaltyPoints(Integer loyaltyPoints) {
        this.setLoyaltyPoints(loyaltyPoints);
        return this;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMostFrequentOrigin() {
        return this.mostFrequentOrigin;
    }

    public UserStatistics mostFrequentOrigin(String mostFrequentOrigin) {
        this.setMostFrequentOrigin(mostFrequentOrigin);
        return this;
    }

    public void setMostFrequentOrigin(String mostFrequentOrigin) {
        this.mostFrequentOrigin = mostFrequentOrigin;
    }

    public String getMostFrequentDestination() {
        return this.mostFrequentDestination;
    }

    public UserStatistics mostFrequentDestination(String mostFrequentDestination) {
        this.setMostFrequentDestination(mostFrequentDestination);
        return this;
    }

    public void setMostFrequentDestination(String mostFrequentDestination) {
        this.mostFrequentDestination = mostFrequentDestination;
    }

    public BigDecimal getAverageTripDistance() {
        return this.averageTripDistance;
    }

    public UserStatistics averageTripDistance(BigDecimal averageTripDistance) {
        this.setAverageTripDistance(averageTripDistance);
        return this;
    }

    public void setAverageTripDistance(BigDecimal averageTripDistance) {
        this.averageTripDistance = averageTripDistance;
    }

    public String getPeakTravelTime() {
        return this.peakTravelTime;
    }

    public UserStatistics peakTravelTime(String peakTravelTime) {
        this.setPeakTravelTime(peakTravelTime);
        return this;
    }

    public void setPeakTravelTime(String peakTravelTime) {
        this.peakTravelTime = peakTravelTime;
    }

    public Integer getWeekendTrips() {
        return this.weekendTrips;
    }

    public UserStatistics weekendTrips(Integer weekendTrips) {
        this.setWeekendTrips(weekendTrips);
        return this;
    }

    public void setWeekendTrips(Integer weekendTrips) {
        this.weekendTrips = weekendTrips;
    }

    public Integer getHolidayTrips() {
        return this.holidayTrips;
    }

    public UserStatistics holidayTrips(Integer holidayTrips) {
        this.setHolidayTrips(holidayTrips);
        return this;
    }

    public void setHolidayTrips(Integer holidayTrips) {
        this.holidayTrips = holidayTrips;
    }

    public Integer getCancelledTrips() {
        return this.cancelledTrips;
    }

    public UserStatistics cancelledTrips(Integer cancelledTrips) {
        this.setCancelledTrips(cancelledTrips);
        return this;
    }

    public void setCancelledTrips(Integer cancelledTrips) {
        this.cancelledTrips = cancelledTrips;
    }

    public BigDecimal getOnTimePerformanceRate() {
        return this.onTimePerformanceRate;
    }

    public UserStatistics onTimePerformanceRate(BigDecimal onTimePerformanceRate) {
        this.setOnTimePerformanceRate(onTimePerformanceRate);
        return this;
    }

    public void setOnTimePerformanceRate(BigDecimal onTimePerformanceRate) {
        this.onTimePerformanceRate = onTimePerformanceRate;
    }

    public String getPreferredSeatTypes() {
        return this.preferredSeatTypes;
    }

    public UserStatistics preferredSeatTypes(String preferredSeatTypes) {
        this.setPreferredSeatTypes(preferredSeatTypes);
        return this;
    }

    public void setPreferredSeatTypes(String preferredSeatTypes) {
        this.preferredSeatTypes = preferredSeatTypes;
    }

    public Integer getMonthlyTripCount() {
        return this.monthlyTripCount;
    }

    public UserStatistics monthlyTripCount(Integer monthlyTripCount) {
        this.setMonthlyTripCount(monthlyTripCount);
        return this;
    }

    public void setMonthlyTripCount(Integer monthlyTripCount) {
        this.monthlyTripCount = monthlyTripCount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public UserStatistics createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public UserStatistics updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public UserStatistics isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public UserStatistics deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public UserStatistics deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public AppUser getUser() {
        return this.user;
    }

    public void setUser(AppUser appUser) {
        if (this.user != null) {
            this.user.setStatistics(null);
        }
        if (appUser != null) {
            appUser.setStatistics(this);
        }
        this.user = appUser;
    }

    public UserStatistics user(AppUser appUser) {
        this.setUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserStatistics)) {
            return false;
        }
        return getId() != null && getId().equals(((UserStatistics) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserStatistics{" +
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
