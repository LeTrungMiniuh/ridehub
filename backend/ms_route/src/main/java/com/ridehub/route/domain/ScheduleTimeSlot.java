package com.ridehub.route.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A ScheduleTimeSlot.
 */
@Entity
@Table(name = "schedule_time_slot")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleTimeSlot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "slot_code", nullable = false, unique = true)
    private String slotCode;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "buffer_minutes")
    private Integer bufferMinutes;

    @Column(name = "sequence")
    private Integer sequence;

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

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "timeSlots", "occasionRule", "route" }, allowSetters = true)
    private Schedule schedule;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScheduleTimeSlot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlotCode() {
        return this.slotCode;
    }

    public ScheduleTimeSlot slotCode(String slotCode) {
        this.setSlotCode(slotCode);
        return this;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public LocalTime getDepartureTime() {
        return this.departureTime;
    }

    public ScheduleTimeSlot departureTime(LocalTime departureTime) {
        this.setDepartureTime(departureTime);
        return this;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return this.arrivalTime;
    }

    public ScheduleTimeSlot arrivalTime(LocalTime arrivalTime) {
        this.setArrivalTime(arrivalTime);
        return this;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getBufferMinutes() {
        return this.bufferMinutes;
    }

    public ScheduleTimeSlot bufferMinutes(Integer bufferMinutes) {
        this.setBufferMinutes(bufferMinutes);
        return this;
    }

    public void setBufferMinutes(Integer bufferMinutes) {
        this.bufferMinutes = bufferMinutes;
    }

    public Integer getSequence() {
        return this.sequence;
    }

    public ScheduleTimeSlot sequence(Integer sequence) {
        this.setSequence(sequence);
        return this;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Boolean getActive() {
        return this.active;
    }

    public ScheduleTimeSlot active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ScheduleTimeSlot createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public ScheduleTimeSlot updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public ScheduleTimeSlot isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public ScheduleTimeSlot deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public ScheduleTimeSlot deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public ScheduleTimeSlot schedule(Schedule schedule) {
        this.setSchedule(schedule);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleTimeSlot)) {
            return false;
        }
        return getId() != null && getId().equals(((ScheduleTimeSlot) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleTimeSlot{" +
            "id=" + getId() +
            ", slotCode='" + getSlotCode() + "'" +
            ", departureTime='" + getDepartureTime() + "'" +
            ", arrivalTime='" + getArrivalTime() + "'" +
            ", bufferMinutes=" + getBufferMinutes() +
            ", sequence=" + getSequence() +
            ", active='" + getActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
