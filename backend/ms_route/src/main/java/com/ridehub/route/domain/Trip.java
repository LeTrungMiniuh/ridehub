package com.ridehub.route.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Trip.
 */
@Entity
@Table(name = "trip")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Trip implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "trip_code", nullable = false, unique = true)
    private String tripCode;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private Instant departureTime;

    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private Instant arrivalTime;

    @NotNull
    @Column(name = "occasion_factor", precision = 21, scale = 2, nullable = false)
    private BigDecimal occasionFactor;

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

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "schedules", "origin", "destination" }, allowSetters = true)
    private Route route;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "seatMap", "vehicleImg" }, allowSetters = true)
    private Vehicle vehicle;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "schedule" }, allowSetters = true)
    private ScheduleTimeSlot slot;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "staff" }, allowSetters = true)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "staff" }, allowSetters = true)
    private Attendant attendant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Trip id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTripCode() {
        return this.tripCode;
    }

    public Trip tripCode(String tripCode) {
        this.setTripCode(tripCode);
        return this;
    }

    public void setTripCode(String tripCode) {
        this.tripCode = tripCode;
    }

    public Instant getDepartureTime() {
        return this.departureTime;
    }

    public Trip departureTime(Instant departureTime) {
        this.setDepartureTime(departureTime);
        return this;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public Instant getArrivalTime() {
        return this.arrivalTime;
    }

    public Trip arrivalTime(Instant arrivalTime) {
        this.setArrivalTime(arrivalTime);
        return this;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getOccasionFactor() {
        return this.occasionFactor;
    }

    public Trip occasionFactor(BigDecimal occasionFactor) {
        this.setOccasionFactor(occasionFactor);
        return this;
    }

    public void setOccasionFactor(BigDecimal occasionFactor) {
        this.occasionFactor = occasionFactor;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Trip createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Trip updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Trip isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Trip deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public Trip deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Trip route(Route route) {
        this.setRoute(route);
        return this;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Trip vehicle(Vehicle vehicle) {
        this.setVehicle(vehicle);
        return this;
    }

    public ScheduleTimeSlot getSlot() {
        return this.slot;
    }

    public void setSlot(ScheduleTimeSlot scheduleTimeSlot) {
        this.slot = scheduleTimeSlot;
    }

    public Trip slot(ScheduleTimeSlot scheduleTimeSlot) {
        this.setSlot(scheduleTimeSlot);
        return this;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Trip driver(Driver driver) {
        this.setDriver(driver);
        return this;
    }

    public Attendant getAttendant() {
        return this.attendant;
    }

    public void setAttendant(Attendant attendant) {
        this.attendant = attendant;
    }

    public Trip attendant(Attendant attendant) {
        this.setAttendant(attendant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Trip)) {
            return false;
        }
        return getId() != null && getId().equals(((Trip) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Trip{" +
            "id=" + getId() +
            ", tripCode='" + getTripCode() + "'" +
            ", departureTime='" + getDepartureTime() + "'" +
            ", arrivalTime='" + getArrivalTime() + "'" +
            ", occasionFactor=" + getOccasionFactor() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
