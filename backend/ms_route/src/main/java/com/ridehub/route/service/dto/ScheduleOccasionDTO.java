package com.ridehub.route.service.dto;

import com.ridehub.route.domain.enumeration.OccasionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.route.domain.ScheduleOccasion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleOccasionDTO implements Serializable {

    private Long id;

    @NotNull
    private OccasionType occasion;

    @NotNull
    private BigDecimal occasionFactor;

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

    public OccasionType getOccasion() {
        return occasion;
    }

    public void setOccasion(OccasionType occasion) {
        this.occasion = occasion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleOccasionDTO)) {
            return false;
        }

        ScheduleOccasionDTO scheduleOccasionDTO = (ScheduleOccasionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scheduleOccasionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleOccasionDTO{" +
            "id=" + getId() +
            ", occasion='" + getOccasion() + "'" +
            ", occasionFactor=" + getOccasionFactor() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
