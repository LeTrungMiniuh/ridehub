package com.ridehub.user.service.criteria;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.user.domain.TripRecommendation} entity. This class is used
 * in {@link com.ridehub.user.web.rest.TripRecommendationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trip-recommendations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TripRecommendationCriteria implements Serializable, Criteria {

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

    private StringFilter origin;

    private StringFilter destination;

    private LocalDateFilter travelDate;

    private LocalTimeFilter preferredTime;

    private StringFilter budgetRange;

    private StringFilter seatPreference;

    private StringFilter recommendedTrips;

    private BigDecimalFilter confidenceScore;

    private BooleanFilter isBooked;

    private IntegerFilter feedbackRating;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter userId;

    private Boolean distinct;

    public TripRecommendationCriteria() {}

    public TripRecommendationCriteria(TripRecommendationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.origin = other.optionalOrigin().map(StringFilter::copy).orElse(null);
        this.destination = other.optionalDestination().map(StringFilter::copy).orElse(null);
        this.travelDate = other.optionalTravelDate().map(LocalDateFilter::copy).orElse(null);
        this.preferredTime = other.optionalPreferredTime().map(LocalTimeFilter::copy).orElse(null);
        this.budgetRange = other.optionalBudgetRange().map(StringFilter::copy).orElse(null);
        this.seatPreference = other.optionalSeatPreference().map(StringFilter::copy).orElse(null);
        this.recommendedTrips = other.optionalRecommendedTrips().map(StringFilter::copy).orElse(null);
        this.confidenceScore = other.optionalConfidenceScore().map(BigDecimalFilter::copy).orElse(null);
        this.isBooked = other.optionalIsBooked().map(BooleanFilter::copy).orElse(null);
        this.feedbackRating = other.optionalFeedbackRating().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TripRecommendationCriteria copy() {
        return new TripRecommendationCriteria(this);
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

    public StringFilter getOrigin() {
        return origin;
    }

    public Optional<StringFilter> optionalOrigin() {
        return Optional.ofNullable(origin);
    }

    public StringFilter origin() {
        if (origin == null) {
            setOrigin(new StringFilter());
        }
        return origin;
    }

    public void setOrigin(StringFilter origin) {
        this.origin = origin;
    }

    public StringFilter getDestination() {
        return destination;
    }

    public Optional<StringFilter> optionalDestination() {
        return Optional.ofNullable(destination);
    }

    public StringFilter destination() {
        if (destination == null) {
            setDestination(new StringFilter());
        }
        return destination;
    }

    public void setDestination(StringFilter destination) {
        this.destination = destination;
    }

    public LocalDateFilter getTravelDate() {
        return travelDate;
    }

    public Optional<LocalDateFilter> optionalTravelDate() {
        return Optional.ofNullable(travelDate);
    }

    public LocalDateFilter travelDate() {
        if (travelDate == null) {
            setTravelDate(new LocalDateFilter());
        }
        return travelDate;
    }

    public void setTravelDate(LocalDateFilter travelDate) {
        this.travelDate = travelDate;
    }

    public LocalTimeFilter getPreferredTime() {
        return preferredTime;
    }

    public Optional<LocalTimeFilter> optionalPreferredTime() {
        return Optional.ofNullable(preferredTime);
    }

    public LocalTimeFilter preferredTime() {
        if (preferredTime == null) {
            setPreferredTime(new LocalTimeFilter());
        }
        return preferredTime;
    }

    public void setPreferredTime(LocalTimeFilter preferredTime) {
        this.preferredTime = preferredTime;
    }

    public StringFilter getBudgetRange() {
        return budgetRange;
    }

    public Optional<StringFilter> optionalBudgetRange() {
        return Optional.ofNullable(budgetRange);
    }

    public StringFilter budgetRange() {
        if (budgetRange == null) {
            setBudgetRange(new StringFilter());
        }
        return budgetRange;
    }

    public void setBudgetRange(StringFilter budgetRange) {
        this.budgetRange = budgetRange;
    }

    public StringFilter getSeatPreference() {
        return seatPreference;
    }

    public Optional<StringFilter> optionalSeatPreference() {
        return Optional.ofNullable(seatPreference);
    }

    public StringFilter seatPreference() {
        if (seatPreference == null) {
            setSeatPreference(new StringFilter());
        }
        return seatPreference;
    }

    public void setSeatPreference(StringFilter seatPreference) {
        this.seatPreference = seatPreference;
    }

    public StringFilter getRecommendedTrips() {
        return recommendedTrips;
    }

    public Optional<StringFilter> optionalRecommendedTrips() {
        return Optional.ofNullable(recommendedTrips);
    }

    public StringFilter recommendedTrips() {
        if (recommendedTrips == null) {
            setRecommendedTrips(new StringFilter());
        }
        return recommendedTrips;
    }

    public void setRecommendedTrips(StringFilter recommendedTrips) {
        this.recommendedTrips = recommendedTrips;
    }

    public BigDecimalFilter getConfidenceScore() {
        return confidenceScore;
    }

    public Optional<BigDecimalFilter> optionalConfidenceScore() {
        return Optional.ofNullable(confidenceScore);
    }

    public BigDecimalFilter confidenceScore() {
        if (confidenceScore == null) {
            setConfidenceScore(new BigDecimalFilter());
        }
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimalFilter confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public BooleanFilter getIsBooked() {
        return isBooked;
    }

    public Optional<BooleanFilter> optionalIsBooked() {
        return Optional.ofNullable(isBooked);
    }

    public BooleanFilter isBooked() {
        if (isBooked == null) {
            setIsBooked(new BooleanFilter());
        }
        return isBooked;
    }

    public void setIsBooked(BooleanFilter isBooked) {
        this.isBooked = isBooked;
    }

    public IntegerFilter getFeedbackRating() {
        return feedbackRating;
    }

    public Optional<IntegerFilter> optionalFeedbackRating() {
        return Optional.ofNullable(feedbackRating);
    }

    public IntegerFilter feedbackRating() {
        if (feedbackRating == null) {
            setFeedbackRating(new IntegerFilter());
        }
        return feedbackRating;
    }

    public void setFeedbackRating(IntegerFilter feedbackRating) {
        this.feedbackRating = feedbackRating;
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

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final TripRecommendationCriteria that = (TripRecommendationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(origin, that.origin) &&
            Objects.equals(destination, that.destination) &&
            Objects.equals(travelDate, that.travelDate) &&
            Objects.equals(preferredTime, that.preferredTime) &&
            Objects.equals(budgetRange, that.budgetRange) &&
            Objects.equals(seatPreference, that.seatPreference) &&
            Objects.equals(recommendedTrips, that.recommendedTrips) &&
            Objects.equals(confidenceScore, that.confidenceScore) &&
            Objects.equals(isBooked, that.isBooked) &&
            Objects.equals(feedbackRating, that.feedbackRating) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            origin,
            destination,
            travelDate,
            preferredTime,
            budgetRange,
            seatPreference,
            recommendedTrips,
            confidenceScore,
            isBooked,
            feedbackRating,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TripRecommendationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrigin().map(f -> "origin=" + f + ", ").orElse("") +
            optionalDestination().map(f -> "destination=" + f + ", ").orElse("") +
            optionalTravelDate().map(f -> "travelDate=" + f + ", ").orElse("") +
            optionalPreferredTime().map(f -> "preferredTime=" + f + ", ").orElse("") +
            optionalBudgetRange().map(f -> "budgetRange=" + f + ", ").orElse("") +
            optionalSeatPreference().map(f -> "seatPreference=" + f + ", ").orElse("") +
            optionalRecommendedTrips().map(f -> "recommendedTrips=" + f + ", ").orElse("") +
            optionalConfidenceScore().map(f -> "confidenceScore=" + f + ", ").orElse("") +
            optionalIsBooked().map(f -> "isBooked=" + f + ", ").orElse("") +
            optionalFeedbackRating().map(f -> "feedbackRating=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
