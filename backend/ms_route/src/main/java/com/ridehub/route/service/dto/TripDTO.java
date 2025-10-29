package com.ridehub.route.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.route.domain.Trip} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripDTO implements Serializable {

    private Long id;

    @NotNull
    private String tripCode;

    @NotNull
    private Instant departureTime;

    @NotNull
    private Instant arrivalTime;

    @NotNull
    private BigDecimal occasionFactor;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    @NotNull
    private RouteDTO route;

    @NotNull
    private VehicleDTO vehicle;

    @NotNull
    private ScheduleTimeSlotDTO slot;

    @NotNull
    private DriverDTO driver;

    private AttendantDTO attendant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTripCode() {
        return tripCode;
    }

    public void setTripCode(String tripCode) {
        this.tripCode = tripCode;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getOccasionFactor() {
        return occasionFactor;
    }

    public void setOccasionFactor(BigDecimal occasionFactor) {
        this.occasionFactor = occasionFactor;
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

    public RouteDTO getRoute() {
        return route;
    }

    public void setRoute(RouteDTO route) {
        this.route = route;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }

    public ScheduleTimeSlotDTO getSlot() {
        return slot;
    }

    public void setSlot(ScheduleTimeSlotDTO slot) {
        this.slot = slot;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    public AttendantDTO getAttendant() {
        return attendant;
    }

    public void setAttendant(AttendantDTO attendant) {
        this.attendant = attendant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TripDTO)) {
            return false;
        }

        TripDTO tripDTO = (TripDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tripDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripDTO{" +
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
            ", route=" + getRoute() +
            ", vehicle=" + getVehicle() +
            ", slot=" + getSlot() +
            ", driver=" + getDriver() +
            ", attendant=" + getAttendant() +
            "}";
    }
}
