package com.ridehub.user.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UserStatisticsCriteriaTest {

    @Test
    void newUserStatisticsCriteriaHasAllFiltersNullTest() {
        var userStatisticsCriteria = new UserStatisticsCriteria();
        assertThat(userStatisticsCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void userStatisticsCriteriaFluentMethodsCreatesFiltersTest() {
        var userStatisticsCriteria = new UserStatisticsCriteria();

        setAllFilters(userStatisticsCriteria);

        assertThat(userStatisticsCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void userStatisticsCriteriaCopyCreatesNullFilterTest() {
        var userStatisticsCriteria = new UserStatisticsCriteria();
        var copy = userStatisticsCriteria.copy();

        assertThat(userStatisticsCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(userStatisticsCriteria)
        );
    }

    @Test
    void userStatisticsCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var userStatisticsCriteria = new UserStatisticsCriteria();
        setAllFilters(userStatisticsCriteria);

        var copy = userStatisticsCriteria.copy();

        assertThat(userStatisticsCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(userStatisticsCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var userStatisticsCriteria = new UserStatisticsCriteria();

        assertThat(userStatisticsCriteria).hasToString("UserStatisticsCriteria{}");
    }

    private static void setAllFilters(UserStatisticsCriteria userStatisticsCriteria) {
        userStatisticsCriteria.id();
        userStatisticsCriteria.totalTrips();
        userStatisticsCriteria.totalSpent();
        userStatisticsCriteria.favoriteRoutes();
        userStatisticsCriteria.preferredVehicleTypes();
        userStatisticsCriteria.averageTripDuration();
        userStatisticsCriteria.lastTravelDate();
        userStatisticsCriteria.bookingFrequency();
        userStatisticsCriteria.loyaltyPoints();
        userStatisticsCriteria.mostFrequentOrigin();
        userStatisticsCriteria.mostFrequentDestination();
        userStatisticsCriteria.averageTripDistance();
        userStatisticsCriteria.peakTravelTime();
        userStatisticsCriteria.weekendTrips();
        userStatisticsCriteria.holidayTrips();
        userStatisticsCriteria.cancelledTrips();
        userStatisticsCriteria.onTimePerformanceRate();
        userStatisticsCriteria.preferredSeatTypes();
        userStatisticsCriteria.monthlyTripCount();
        userStatisticsCriteria.createdAt();
        userStatisticsCriteria.updatedAt();
        userStatisticsCriteria.isDeleted();
        userStatisticsCriteria.deletedAt();
        userStatisticsCriteria.deletedBy();
        userStatisticsCriteria.userId();
        userStatisticsCriteria.distinct();
    }

    private static Condition<UserStatisticsCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTotalTrips()) &&
                condition.apply(criteria.getTotalSpent()) &&
                condition.apply(criteria.getFavoriteRoutes()) &&
                condition.apply(criteria.getPreferredVehicleTypes()) &&
                condition.apply(criteria.getAverageTripDuration()) &&
                condition.apply(criteria.getLastTravelDate()) &&
                condition.apply(criteria.getBookingFrequency()) &&
                condition.apply(criteria.getLoyaltyPoints()) &&
                condition.apply(criteria.getMostFrequentOrigin()) &&
                condition.apply(criteria.getMostFrequentDestination()) &&
                condition.apply(criteria.getAverageTripDistance()) &&
                condition.apply(criteria.getPeakTravelTime()) &&
                condition.apply(criteria.getWeekendTrips()) &&
                condition.apply(criteria.getHolidayTrips()) &&
                condition.apply(criteria.getCancelledTrips()) &&
                condition.apply(criteria.getOnTimePerformanceRate()) &&
                condition.apply(criteria.getPreferredSeatTypes()) &&
                condition.apply(criteria.getMonthlyTripCount()) &&
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

    private static Condition<UserStatisticsCriteria> copyFiltersAre(
        UserStatisticsCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTotalTrips(), copy.getTotalTrips()) &&
                condition.apply(criteria.getTotalSpent(), copy.getTotalSpent()) &&
                condition.apply(criteria.getFavoriteRoutes(), copy.getFavoriteRoutes()) &&
                condition.apply(criteria.getPreferredVehicleTypes(), copy.getPreferredVehicleTypes()) &&
                condition.apply(criteria.getAverageTripDuration(), copy.getAverageTripDuration()) &&
                condition.apply(criteria.getLastTravelDate(), copy.getLastTravelDate()) &&
                condition.apply(criteria.getBookingFrequency(), copy.getBookingFrequency()) &&
                condition.apply(criteria.getLoyaltyPoints(), copy.getLoyaltyPoints()) &&
                condition.apply(criteria.getMostFrequentOrigin(), copy.getMostFrequentOrigin()) &&
                condition.apply(criteria.getMostFrequentDestination(), copy.getMostFrequentDestination()) &&
                condition.apply(criteria.getAverageTripDistance(), copy.getAverageTripDistance()) &&
                condition.apply(criteria.getPeakTravelTime(), copy.getPeakTravelTime()) &&
                condition.apply(criteria.getWeekendTrips(), copy.getWeekendTrips()) &&
                condition.apply(criteria.getHolidayTrips(), copy.getHolidayTrips()) &&
                condition.apply(criteria.getCancelledTrips(), copy.getCancelledTrips()) &&
                condition.apply(criteria.getOnTimePerformanceRate(), copy.getOnTimePerformanceRate()) &&
                condition.apply(criteria.getPreferredSeatTypes(), copy.getPreferredSeatTypes()) &&
                condition.apply(criteria.getMonthlyTripCount(), copy.getMonthlyTripCount()) &&
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
