package com.ridehub.route.service.dto;

import com.ridehub.route.domain.enumeration.OccasionType;
import com.ridehub.route.domain.enumeration.SeatType;
import com.ridehub.route.domain.enumeration.VehicleType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.route.domain.PricingTemplate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PricingTemplateDTO implements Serializable {

    private Long id;

    @NotNull
    private VehicleType vehicleType;

    @NotNull
    private SeatType seatType;

    @NotNull
    private OccasionType occasionType;

    @NotNull
    private BigDecimal baseFare;

    private BigDecimal vehicleFactor;

    private BigDecimal floorFactor;

    private BigDecimal seatFactor;

    @NotNull
    private BigDecimal occasionFactor;

    @NotNull
    private BigDecimal finalPrice;

    @NotNull
    private LocalDate validFrom;

    private LocalDate validTo;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    @NotNull
    private RouteDTO route;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public OccasionType getOccasionType() {
        return occasionType;
    }

    public void setOccasionType(OccasionType occasionType) {
        this.occasionType = occasionType;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getVehicleFactor() {
        return vehicleFactor;
    }

    public void setVehicleFactor(BigDecimal vehicleFactor) {
        this.vehicleFactor = vehicleFactor;
    }

    public BigDecimal getFloorFactor() {
        return floorFactor;
    }

    public void setFloorFactor(BigDecimal floorFactor) {
        this.floorFactor = floorFactor;
    }

    public BigDecimal getSeatFactor() {
        return seatFactor;
    }

    public void setSeatFactor(BigDecimal seatFactor) {
        this.seatFactor = seatFactor;
    }

    public BigDecimal getOccasionFactor() {
        return occasionFactor;
    }

    public void setOccasionFactor(BigDecimal occasionFactor) {
        this.occasionFactor = occasionFactor;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
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

    public RouteDTO getRoute() {
        return route;
    }

    public void setRoute(RouteDTO route) {
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PricingTemplateDTO)) {
            return false;
        }

        PricingTemplateDTO pricingTemplateDTO = (PricingTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pricingTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PricingTemplateDTO{" +
            "id=" + getId() +
            ", vehicleType='" + getVehicleType() + "'" +
            ", seatType='" + getSeatType() + "'" +
            ", occasionType='" + getOccasionType() + "'" +
            ", baseFare=" + getBaseFare() +
            ", vehicleFactor=" + getVehicleFactor() +
            ", floorFactor=" + getFloorFactor() +
            ", seatFactor=" + getSeatFactor() +
            ", occasionFactor=" + getOccasionFactor() +
            ", finalPrice=" + getFinalPrice() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", route=" + getRoute() +
            "}";
    }
}
