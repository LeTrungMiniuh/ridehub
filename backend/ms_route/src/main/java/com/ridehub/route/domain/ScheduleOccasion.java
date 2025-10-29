package com.ridehub.route.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ridehub.route.domain.enumeration.OccasionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A ScheduleOccasion.
 */
@Entity
@Table(name = "schedule_occasion")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleOccasion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "occasion", nullable = false, unique = true)
    private OccasionType occasion;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "occasionRule")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "timeSlots", "occasionRule", "route" }, allowSetters = true)
    private Set<Schedule> schedules = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScheduleOccasion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OccasionType getOccasion() {
        return this.occasion;
    }

    public ScheduleOccasion occasion(OccasionType occasion) {
        this.setOccasion(occasion);
        return this;
    }

    public void setOccasion(OccasionType occasion) {
        this.occasion = occasion;
    }

    public BigDecimal getOccasionFactor() {
        return this.occasionFactor;
    }

    public ScheduleOccasion occasionFactor(BigDecimal occasionFactor) {
        this.setOccasionFactor(occasionFactor);
        return this;
    }

    public void setOccasionFactor(BigDecimal occasionFactor) {
        this.occasionFactor = occasionFactor;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ScheduleOccasion createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public ScheduleOccasion updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public ScheduleOccasion isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public ScheduleOccasion deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public ScheduleOccasion deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Set<Schedule> getSchedules() {
        return this.schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        if (this.schedules != null) {
            this.schedules.forEach(i -> i.setOccasionRule(null));
        }
        if (schedules != null) {
            schedules.forEach(i -> i.setOccasionRule(this));
        }
        this.schedules = schedules;
    }

    public ScheduleOccasion schedules(Set<Schedule> schedules) {
        this.setSchedules(schedules);
        return this;
    }

    public ScheduleOccasion addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setOccasionRule(this);
        return this;
    }

    public ScheduleOccasion removeSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
        schedule.setOccasionRule(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleOccasion)) {
            return false;
        }
        return getId() != null && getId().equals(((ScheduleOccasion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleOccasion{" +
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
