package com.ridehub.route.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.Schedule} entity. This class is used
 * in {@link com.ridehub.route.web.rest.ScheduleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /schedules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter scheduleCode;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private StringFilter daysOfWeek;

    private BooleanFilter active;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter timeSlotsId;

    private LongFilter occasionRuleId;

    private LongFilter routeId;

    private Boolean distinct;

    public ScheduleCriteria() {}

    public ScheduleCriteria(ScheduleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.scheduleCode = other.optionalScheduleCode().map(StringFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(LocalDateFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(LocalDateFilter::copy).orElse(null);
        this.daysOfWeek = other.optionalDaysOfWeek().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.timeSlotsId = other.optionalTimeSlotsId().map(LongFilter::copy).orElse(null);
        this.occasionRuleId = other.optionalOccasionRuleId().map(LongFilter::copy).orElse(null);
        this.routeId = other.optionalRouteId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScheduleCriteria copy() {
        return new ScheduleCriteria(this);
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

    public StringFilter getScheduleCode() {
        return scheduleCode;
    }

    public Optional<StringFilter> optionalScheduleCode() {
        return Optional.ofNullable(scheduleCode);
    }

    public StringFilter scheduleCode() {
        if (scheduleCode == null) {
            setScheduleCode(new StringFilter());
        }
        return scheduleCode;
    }

    public void setScheduleCode(StringFilter scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public Optional<LocalDateFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public LocalDateFilter startDate() {
        if (startDate == null) {
            setStartDate(new LocalDateFilter());
        }
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public LocalDateFilter getEndDate() {
        return endDate;
    }

    public Optional<LocalDateFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public LocalDateFilter endDate() {
        if (endDate == null) {
            setEndDate(new LocalDateFilter());
        }
        return endDate;
    }

    public void setEndDate(LocalDateFilter endDate) {
        this.endDate = endDate;
    }

    public StringFilter getDaysOfWeek() {
        return daysOfWeek;
    }

    public Optional<StringFilter> optionalDaysOfWeek() {
        return Optional.ofNullable(daysOfWeek);
    }

    public StringFilter daysOfWeek() {
        if (daysOfWeek == null) {
            setDaysOfWeek(new StringFilter());
        }
        return daysOfWeek;
    }

    public void setDaysOfWeek(StringFilter daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
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

    public LongFilter getTimeSlotsId() {
        return timeSlotsId;
    }

    public Optional<LongFilter> optionalTimeSlotsId() {
        return Optional.ofNullable(timeSlotsId);
    }

    public LongFilter timeSlotsId() {
        if (timeSlotsId == null) {
            setTimeSlotsId(new LongFilter());
        }
        return timeSlotsId;
    }

    public void setTimeSlotsId(LongFilter timeSlotsId) {
        this.timeSlotsId = timeSlotsId;
    }

    public LongFilter getOccasionRuleId() {
        return occasionRuleId;
    }

    public Optional<LongFilter> optionalOccasionRuleId() {
        return Optional.ofNullable(occasionRuleId);
    }

    public LongFilter occasionRuleId() {
        if (occasionRuleId == null) {
            setOccasionRuleId(new LongFilter());
        }
        return occasionRuleId;
    }

    public void setOccasionRuleId(LongFilter occasionRuleId) {
        this.occasionRuleId = occasionRuleId;
    }

    public LongFilter getRouteId() {
        return routeId;
    }

    public Optional<LongFilter> optionalRouteId() {
        return Optional.ofNullable(routeId);
    }

    public LongFilter routeId() {
        if (routeId == null) {
            setRouteId(new LongFilter());
        }
        return routeId;
    }

    public void setRouteId(LongFilter routeId) {
        this.routeId = routeId;
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
        final ScheduleCriteria that = (ScheduleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(scheduleCode, that.scheduleCode) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(daysOfWeek, that.daysOfWeek) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(timeSlotsId, that.timeSlotsId) &&
            Objects.equals(occasionRuleId, that.occasionRuleId) &&
            Objects.equals(routeId, that.routeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            scheduleCode,
            startDate,
            endDate,
            daysOfWeek,
            active,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            timeSlotsId,
            occasionRuleId,
            routeId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalScheduleCode().map(f -> "scheduleCode=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalDaysOfWeek().map(f -> "daysOfWeek=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalTimeSlotsId().map(f -> "timeSlotsId=" + f + ", ").orElse("") +
            optionalOccasionRuleId().map(f -> "occasionRuleId=" + f + ", ").orElse("") +
            optionalRouteId().map(f -> "routeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
