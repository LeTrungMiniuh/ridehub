package com.ridehub.route.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.route.domain.Schedule} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleDTO implements Serializable {

    private Long id;

    @NotNull
    private String scheduleCode;

    private LocalDate startDate;

    private LocalDate endDate;

    private String daysOfWeek;

    @NotNull
    private Boolean active;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    @NotNull
    private ScheduleOccasionDTO occasionRule;

    @NotNull
    private RouteDTO route;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScheduleCode() {
        return scheduleCode;
    }

    public void setScheduleCode(String scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public ScheduleOccasionDTO getOccasionRule() {
        return occasionRule;
    }

    public void setOccasionRule(ScheduleOccasionDTO occasionRule) {
        this.occasionRule = occasionRule;
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
        if (!(o instanceof ScheduleDTO)) {
            return false;
        }

        ScheduleDTO scheduleDTO = (ScheduleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scheduleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleDTO{" +
            "id=" + getId() +
            ", scheduleCode='" + getScheduleCode() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", daysOfWeek='" + getDaysOfWeek() + "'" +
            ", active='" + getActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", occasionRule=" + getOccasionRule() +
            ", route=" + getRoute() +
            "}";
    }
}
