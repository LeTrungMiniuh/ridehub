package com.ridehub.user.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TripRecommendationCriteriaTest {

    @Test
    void newTripRecommendationCriteriaHasAllFiltersNullTest() {
        var tripRecommendationCriteria = new TripRecommendationCriteria();
        assertThat(tripRecommendationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tripRecommendationCriteriaFluentMethodsCreatesFiltersTest() {
        var tripRecommendationCriteria = new TripRecommendationCriteria();

        setAllFilters(tripRecommendationCriteria);

        assertThat(tripRecommendationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tripRecommendationCriteriaCopyCreatesNullFilterTest() {
        var tripRecommendationCriteria = new TripRecommendationCriteria();
        var copy = tripRecommendationCriteria.copy();

        assertThat(tripRecommendationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tripRecommendationCriteria)
        );
    }

    @Test
    void tripRecommendationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tripRecommendationCriteria = new TripRecommendationCriteria();
        setAllFilters(tripRecommendationCriteria);

        var copy = tripRecommendationCriteria.copy();

        assertThat(tripRecommendationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tripRecommendationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tripRecommendationCriteria = new TripRecommendationCriteria();

        assertThat(tripRecommendationCriteria).hasToString("TripRecommendationCriteria{}");
    }

    private static void setAllFilters(TripRecommendationCriteria tripRecommendationCriteria) {
        tripRecommendationCriteria.id();
        tripRecommendationCriteria.origin();
        tripRecommendationCriteria.destination();
        tripRecommendationCriteria.travelDate();
        tripRecommendationCriteria.preferredTime();
        tripRecommendationCriteria.budgetRange();
        tripRecommendationCriteria.seatPreference();
        tripRecommendationCriteria.recommendedTrips();
        tripRecommendationCriteria.confidenceScore();
        tripRecommendationCriteria.isBooked();
        tripRecommendationCriteria.feedbackRating();
        tripRecommendationCriteria.createdAt();
        tripRecommendationCriteria.updatedAt();
        tripRecommendationCriteria.isDeleted();
        tripRecommendationCriteria.deletedAt();
        tripRecommendationCriteria.deletedBy();
        tripRecommendationCriteria.userId();
        tripRecommendationCriteria.distinct();
    }

    private static Condition<TripRecommendationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOrigin()) &&
                condition.apply(criteria.getDestination()) &&
                condition.apply(criteria.getTravelDate()) &&
                condition.apply(criteria.getPreferredTime()) &&
                condition.apply(criteria.getBudgetRange()) &&
                condition.apply(criteria.getSeatPreference()) &&
                condition.apply(criteria.getRecommendedTrips()) &&
                condition.apply(criteria.getConfidenceScore()) &&
                condition.apply(criteria.getIsBooked()) &&
                condition.apply(criteria.getFeedbackRating()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TripRecommendationCriteria> copyFiltersAre(
        TripRecommendationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOrigin(), copy.getOrigin()) &&
                condition.apply(criteria.getDestination(), copy.getDestination()) &&
                condition.apply(criteria.getTravelDate(), copy.getTravelDate()) &&
                condition.apply(criteria.getPreferredTime(), copy.getPreferredTime()) &&
                condition.apply(criteria.getBudgetRange(), copy.getBudgetRange()) &&
                condition.apply(criteria.getSeatPreference(), copy.getSeatPreference()) &&
                condition.apply(criteria.getRecommendedTrips(), copy.getRecommendedTrips()) &&
                condition.apply(criteria.getConfidenceScore(), copy.getConfidenceScore()) &&
                condition.apply(criteria.getIsBooked(), copy.getIsBooked()) &&
                condition.apply(criteria.getFeedbackRating(), copy.getFeedbackRating()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
