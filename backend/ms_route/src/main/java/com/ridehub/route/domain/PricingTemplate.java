package com.ridehub.route.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ridehub.route.domain.enumeration.OccasionType;
import com.ridehub.route.domain.enumeration.SeatType;
import com.ridehub.route.domain.enumeration.VehicleType;
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
 * A PricingTemplate.
 */
@Entity
@Table(name = "pricing_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PricingTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "occasion_type", nullable = false)
    private OccasionType occasionType;

    @NotNull
    @Column(name = "base_fare", precision = 21, scale = 2, nullable = false)
    private BigDecimal baseFare;

    @Column(name = "vehicle_factor", precision = 21, scale = 2)
    private BigDecimal vehicleFactor;

    @Column(name = "floor_factor", precision = 21, scale = 2)
    private BigDecimal floorFactor;

    @Column(name = "seat_factor", precision = 21, scale = 2)
    private BigDecimal seatFactor;

    @NotNull
    @Column(name = "occasion_factor", precision = 21, scale = 2, nullable = false)
    private BigDecimal occasionFactor;

    @NotNull
    @Column(name = "final_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal finalPrice;

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

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "schedules", "origin", "destination" }, allowSetters = true)
    private Route route;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PricingTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public PricingTemplate vehicleType(VehicleType vehicleType) {
        this.setVehicleType(vehicleType);
        return this;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public SeatType getSeatType() {
        return this.seatType;
    }

    public PricingTemplate seatType(SeatType seatType) {
        this.setSeatType(seatType);
        return this;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public OccasionType getOccasionType() {
        return this.occasionType;
    }

    public PricingTemplate occasionType(OccasionType occasionType) {
        this.setOccasionType(occasionType);
        return this;
    }

    public void setOccasionType(OccasionType occasionType) {
        this.occasionType = occasionType;
    }

    public BigDecimal getBaseFare() {
        return this.baseFare;
    }

    public PricingTemplate baseFare(BigDecimal baseFare) {
        this.setBaseFare(baseFare);
        return this;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getVehicleFactor() {
        return this.vehicleFactor;
    }

    public PricingTemplate vehicleFactor(BigDecimal vehicleFactor) {
        this.setVehicleFactor(vehicleFactor);
        return this;
    }

    public void setVehicleFactor(BigDecimal vehicleFactor) {
        this.vehicleFactor = vehicleFactor;
    }

    public BigDecimal getFloorFactor() {
        return this.floorFactor;
    }

    public PricingTemplate floorFactor(BigDecimal floorFactor) {
        this.setFloorFactor(floorFactor);
        return this;
    }

    public void setFloorFactor(BigDecimal floorFactor) {
        this.floorFactor = floorFactor;
    }

    public BigDecimal getSeatFactor() {
        return this.seatFactor;
    }

    public PricingTemplate seatFactor(BigDecimal seatFactor) {
        this.setSeatFactor(seatFactor);
        return this;
    }

    public void setSeatFactor(BigDecimal seatFactor) {
        this.seatFactor = seatFactor;
    }

    public BigDecimal getOccasionFactor() {
        return this.occasionFactor;
    }

    public PricingTemplate occasionFactor(BigDecimal occasionFactor) {
        this.setOccasionFactor(occasionFactor);
        return this;
    }

    public void setOccasionFactor(BigDecimal occasionFactor) {
        this.occasionFactor = occasionFactor;
    }

    public BigDecimal getFinalPrice() {
        return this.finalPrice;
    }

    public PricingTemplate finalPrice(BigDecimal finalPrice) {
        this.setFinalPrice(finalPrice);
        return this;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public LocalDate getValidFrom() {
        return this.validFrom;
    }

    public PricingTemplate validFrom(LocalDate validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return this.validTo;
    }

    public PricingTemplate validTo(LocalDate validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PricingTemplate createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PricingTemplate updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public PricingTemplate isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public PricingTemplate deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public PricingTemplate deletedBy(UUID deletedBy) {
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

    public PricingTemplate route(Route route) {
        this.setRoute(route);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PricingTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((PricingTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PricingTemplate{" +
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
            "}";
    }
}
