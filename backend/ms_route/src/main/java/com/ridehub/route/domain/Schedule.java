package com.ridehub.route.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Schedule.
 */
@Entity
@Table(name = "schedule")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "schedule_code", nullable = false, unique = true)
    private String scheduleCode;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Pattern(regexp = "^[1-7,]*$", message = "Days must be comma-separated numbers 1-7 (Monday=1, Sunday=7)")
    @Column(name = "days_of_week")
    private String daysOfWeek;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schedule")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "schedule" }, allowSetters = true)
    private Set<ScheduleTimeSlot> timeSlots = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "schedules" }, allowSetters = true)
    private ScheduleOccasion occasionRule;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "schedules", "origin", "destination" }, allowSetters = true)
    private Route route;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Schedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScheduleCode() {
        return this.scheduleCode;
    }

    public Schedule scheduleCode(String scheduleCode) {
        this.setScheduleCode(scheduleCode);
        return this;
    }

    public void setScheduleCode(String scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Schedule startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Schedule endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDaysOfWeek() {
        return this.daysOfWeek;
    }

    public Schedule daysOfWeek(String daysOfWeek) {
        this.setDaysOfWeek(daysOfWeek);
        return this;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Schedule active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Schedule createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Schedule updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Schedule isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public Schedule deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public Schedule deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Set<ScheduleTimeSlot> getTimeSlots() {
        return this.timeSlots;
    }

    public void setTimeSlots(Set<ScheduleTimeSlot> scheduleTimeSlots) {
        if (this.timeSlots != null) {
            this.timeSlots.forEach(i -> i.setSchedule(null));
        }
        if (scheduleTimeSlots != null) {
            scheduleTimeSlots.forEach(i -> i.setSchedule(this));
        }
        this.timeSlots = scheduleTimeSlots;
    }

    public Schedule timeSlots(Set<ScheduleTimeSlot> scheduleTimeSlots) {
        this.setTimeSlots(scheduleTimeSlots);
        return this;
    }

    public Schedule addTimeSlots(ScheduleTimeSlot scheduleTimeSlot) {
        this.timeSlots.add(scheduleTimeSlot);
        scheduleTimeSlot.setSchedule(this);
        return this;
    }

    public Schedule removeTimeSlots(ScheduleTimeSlot scheduleTimeSlot) {
        this.timeSlots.remove(scheduleTimeSlot);
        scheduleTimeSlot.setSchedule(null);
        return this;
    }

    public ScheduleOccasion getOccasionRule() {
        return this.occasionRule;
    }

    public void setOccasionRule(ScheduleOccasion scheduleOccasion) {
        this.occasionRule = scheduleOccasion;
    }

    public Schedule occasionRule(ScheduleOccasion scheduleOccasion) {
        this.setOccasionRule(scheduleOccasion);
        return this;
    }

    public Route getRoute() {
        return this.route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Schedule route(Route route) {
        this.setRoute(route);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }
        return getId() != null && getId().equals(((Schedule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Schedule{" +
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
            "}";
    }
}
