package com.ridehub.route.service.criteria;

import com.ridehub.route.domain.enumeration.OccasionType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.route.domain.ScheduleOccasion} entity. This class is used
 * in {@link com.ridehub.route.web.rest.ScheduleOccasionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /schedule-occasions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleOccasionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OccasionType
     */
    public static class OccasionTypeFilter extends Filter<OccasionType> {

        public OccasionTypeFilter() {}

        public OccasionTypeFilter(OccasionTypeFilter filter) {
            super(filter);
        }

        @Override
        public OccasionTypeFilter copy() {
            return new OccasionTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private OccasionTypeFilter occasion;

    private BigDecimalFilter occasionFactor;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter scheduleId;

    private Boolean distinct;

    public ScheduleOccasionCriteria() {}

    public ScheduleOccasionCriteria(ScheduleOccasionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.occasion = other.optionalOccasion().map(OccasionTypeFilter::copy).orElse(null);
        this.occasionFactor = other.optionalOccasionFactor().map(BigDecimalFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.scheduleId = other.optionalScheduleId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScheduleOccasionCriteria copy() {
        return new ScheduleOccasionCriteria(this);
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

    public OccasionTypeFilter getOccasion() {
        return occasion;
    }

    public Optional<OccasionTypeFilter> optionalOccasion() {
        return Optional.ofNullable(occasion);
    }

    public OccasionTypeFilter occasion() {
        if (occasion == null) {
            setOccasion(new OccasionTypeFilter());
        }
        return occasion;
    }

    public void setOccasion(OccasionTypeFilter occasion) {
        this.occasion = occasion;
    }

    public BigDecimalFilter getOccasionFactor() {
        return occasionFactor;
    }

    public Optional<BigDecimalFilter> optionalOccasionFactor() {
        return Optional.ofNullable(occasionFactor);
    }

    public BigDecimalFilter occasionFactor() {
        if (occasionFactor == null) {
            setOccasionFactor(new BigDecimalFilter());
        }
        return occasionFactor;
    }

    public void setOccasionFactor(BigDecimalFilter occasionFactor) {
        this.occasionFactor = occasionFactor;
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
        final ScheduleOccasionCriteria that = (ScheduleOccasionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(occasion, that.occasion) &&
            Objects.equals(occasionFactor, that.occasionFactor) &&
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
        return Objects.hash(id, occasion, occasionFactor, createdAt, updatedAt, isDeleted, deletedAt, deletedBy, scheduleId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleOccasionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOccasion().map(f -> "occasion=" + f + ", ").orElse("") +
            optionalOccasionFactor().map(f -> "occasionFactor=" + f + ", ").orElse("") +
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
