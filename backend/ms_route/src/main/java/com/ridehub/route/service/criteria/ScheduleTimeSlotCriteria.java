package com.ridehub.route.service.criteria;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.ScheduleTimeSlot} entity. This class is used
 * in {@link com.ridehub.route.web.rest.ScheduleTimeSlotResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /schedule-time-slots?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleTimeSlotCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LocalTime
     */
    public static class LocalTimeFilter extends RangeFilter<LocalTime> {

        public LocalTimeFilter() {}

        public LocalTimeFilter(LocalTimeFilter filter) {
            super(filter);
        }

        @Override
        public LocalTimeFilter copy() {
            return new LocalTimeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter slotCode;

    private LocalTimeFilter departureTime;

    private LocalTimeFilter arrivalTime;

    private IntegerFilter bufferMinutes;

    private IntegerFilter sequence;

    private BooleanFilter active;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter scheduleId;

    private Boolean distinct;

    public ScheduleTimeSlotCriteria() {}

    public ScheduleTimeSlotCriteria(ScheduleTimeSlotCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.slotCode = other.optionalSlotCode().map(StringFilter::copy).orElse(null);
        this.departureTime = other.optionalDepartureTime().map(LocalTimeFilter::copy).orElse(null);
        this.arrivalTime = other.optionalArrivalTime().map(LocalTimeFilter::copy).orElse(null);
        this.bufferMinutes = other.optionalBufferMinutes().map(IntegerFilter::copy).orElse(null);
        this.sequence = other.optionalSequence().map(IntegerFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.scheduleId = other.optionalScheduleId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScheduleTimeSlotCriteria copy() {
        return new ScheduleTimeSlotCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSlotCode() {
        return slotCode;
    }

    public Optional<StringFilter> optionalSlotCode() {
        return Optional.ofNullable(slotCode);
    }

    public StringFilter slotCode() {
        if (slotCode == null) {
            setSlotCode(new StringFilter());
        }
        return slotCode;
    }

    public void setSlotCode(StringFilter slotCode) {
        this.slotCode = slotCode;
    }

    public LocalTimeFilter getDepartureTime() {
        return departureTime;
    }

    public Optional<LocalTimeFilter> optionalDepartureTime() {
        return Optional.ofNullable(departureTime);
    }

    public LocalTimeFilter departureTime() {
        if (departureTime == null) {
            setDepartureTime(new LocalTimeFilter());
        }
        return departureTime;
    }

    public void setDepartureTime(LocalTimeFilter departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTimeFilter getArrivalTime() {
        return arrivalTime;
    }

    public Optional<LocalTimeFilter> optionalArrivalTime() {
        return Optional.ofNullable(arrivalTime);
    }

    public LocalTimeFilter arrivalTime() {
        if (arrivalTime == null) {
            setArrivalTime(new LocalTimeFilter());
        }
        return arrivalTime;
    }

    public void setArrivalTime(LocalTimeFilter arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public IntegerFilter getBufferMinutes() {
        return bufferMinutes;
    }

    public Optional<IntegerFilter> optionalBufferMinutes() {
        return Optional.ofNullable(bufferMinutes);
    }

    public IntegerFilter bufferMinutes() {
        if (bufferMinutes == null) {
            setBufferMinutes(new IntegerFilter());
        }
        return bufferMinutes;
    }

    public void setBufferMinutes(IntegerFilter bufferMinutes) {
        this.bufferMinutes = bufferMinutes;
    }

    public IntegerFilter getSequence() {
        return sequence;
    }

    public Optional<IntegerFilter> optionalSequence() {
        return Optional.ofNullable(sequence);
    }

    public IntegerFilter sequence() {
        if (sequence == null) {
            setSequence(new IntegerFilter());
        }
        return sequence;
    }

    public void setSequence(IntegerFilter sequence) {
        this.sequence = sequence;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public InstantFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<InstantFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public InstantFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new InstantFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUIDFilter getDeletedBy() {
        return deletedBy;
    }

    public Optional<UUIDFilter> optionalDeletedBy() {
        return Optional.ofNullable(deletedBy);
    }

    public UUIDFilter deletedBy() {
        if (deletedBy == null) {
            setDeletedBy(new UUIDFilter());
        }
        return deletedBy;
    }

    public void setDeletedBy(UUIDFilter deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LongFilter getScheduleId() {
        return scheduleId;
    }

    public Optional<LongFilter> optionalScheduleId() {
        return Optional.ofNullable(scheduleId);
    }

    public LongFilter scheduleId() {
        if (scheduleId == null) {
            setScheduleId(new LongFilter());
        }
        return scheduleId;
    }

    public void setScheduleId(LongFilter scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ScheduleTimeSlotCriteria that = (ScheduleTimeSlotCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(slotCode, that.slotCode) &&
            Objects.equals(departureTime, that.departureTime) &&
            Objects.equals(arrivalTime, that.arrivalTime) &&
            Objects.equals(bufferMinutes, that.bufferMinutes) &&
            Objects.equals(sequence, that.sequence) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(scheduleId, that.scheduleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            slotCode,
            departureTime,
            arrivalTime,
            bufferMinutes,
            sequence,
            active,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            scheduleId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleTimeSlotCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSlotCode().map(f -> "slotCode=" + f + ", ").orElse("") +
            optionalDepartureTime().map(f -> "departureTime=" + f + ", ").orElse("") +
            optionalArrivalTime().map(f -> "arrivalTime=" + f + ", ").orElse("") +
            optionalBufferMinutes().map(f -> "bufferMinutes=" + f + ", ").orElse("") +
            optionalSequence().map(f -> "sequence=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalScheduleId().map(f -> "scheduleId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
