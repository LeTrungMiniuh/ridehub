package com.ridehub.user.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TripStatisticsCriteriaTest {

    @Test
    void newTripStatisticsCriteriaHasAllFiltersNullTest() {
        var tripStatisticsCriteria = new TripStatisticsCriteria();
        assertThat(tripStatisticsCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tripStatisticsCriteriaFluentMethodsCreatesFiltersTest() {
        var tripStatisticsCriteria = new TripStatisticsCriteria();

        setAllFilters(tripStatisticsCriteria);

        assertThat(tripStatisticsCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tripStatisticsCriteriaCopyCreatesNullFilterTest() {
        var tripStatisticsCriteria = new TripStatisticsCriteria();
        var copy = tripStatisticsCriteria.copy();

        assertThat(tripStatisticsCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tripStatisticsCriteria)
        );
    }

    @Test
    void tripStatisticsCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tripStatisticsCriteria = new TripStatisticsCriteria();
        setAllFilters(tripStatisticsCriteria);

        var copy = tripStatisticsCriteria.copy();

        assertThat(tripStatisticsCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tripStatisticsCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tripStatisticsCriteria = new TripStatisticsCriteria();

        assertThat(tripStatisticsCriteria).hasToString("TripStatisticsCriteria{}");
    }

    private static void setAllFilters(TripStatisticsCriteria tripStatisticsCriteria) {
        tripStatisticsCriteria.id();
        tripStatisticsCriteria.routeId();
        tripStatisticsCriteria.vehicleType();
        tripStatisticsCriteria.occasionType();
        tripStatisticsCriteria.totalBookings();
        tripStatisticsCriteria.totalRevenue();
        tripStatisticsCriteria.averagePrice();
        tripStatisticsCriteria.occupancyRate();
        tripStatisticsCriteria.popularSeatTypes();
        tripStatisticsCriteria.peakTravelTimes();
        tripStatisticsCriteria.cancellationRate();
        tripStatisticsCriteria.customerSatisfactionScore();
        tripStatisticsCriteria.monthlyTrend();
        tripStatisticsCriteria.validFrom();
        tripStatisticsCriteria.validTo();
        tripStatisticsCriteria.createdAt();
        tripStatisticsCriteria.updatedAt();
        tripStatisticsCriteria.isDeleted();
        tripStatisticsCriteria.deletedAt();
        tripStatisticsCriteria.deletedBy();
        tripStatisticsCriteria.distinct();
    }

    private static Condition<TripStatisticsCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRouteId()) &&
                condition.apply(criteria.getVehicleType()) &&
                condition.apply(criteria.getOccasionType()) &&
                condition.apply(criteria.getTotalBookings()) &&
                condition.apply(criteria.getTotalRevenue()) &&
                condition.apply(criteria.getAveragePrice()) &&
                condition.apply(criteria.getOccupancyRate()) &&
                condition.apply(criteria.getPopularSeatTypes()) &&
                condition.apply(criteria.getPeakTravelTimes()) &&
                condition.apply(criteria.getCancellationRate()) &&
                condition.apply(criteria.getCustomerSatisfactionScore()) &&
                condition.apply(criteria.getMonthlyTrend()) &&
                condition.apply(criteria.getValidFrom()) &&
                condition.apply(criteria.getValidTo()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TripStatisticsCriteria> copyFiltersAre(
        TripStatisticsCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRouteId(), copy.getRouteId()) &&
                condition.apply(criteria.getVehicleType(), copy.getVehicleType()) &&
                condition.apply(criteria.getOccasionType(), copy.getOccasionType()) &&
                condition.apply(criteria.getTotalBookings(), copy.getTotalBookings()) &&
                condition.apply(criteria.getTotalRevenue(), copy.getTotalRevenue()) &&
                condition.apply(criteria.getAveragePrice(), copy.getAveragePrice()) &&
                condition.apply(criteria.getOccupancyRate(), copy.getOccupancyRate()) &&
                condition.apply(criteria.getPopularSeatTypes(), copy.getPopularSeatTypes()) &&
                condition.apply(criteria.getPeakTravelTimes(), copy.getPeakTravelTimes()) &&
                condition.apply(criteria.getCancellationRate(), copy.getCancellationRate()) &&
                condition.apply(criteria.getCustomerSatisfactionScore(), copy.getCustomerSatisfactionScore()) &&
                condition.apply(criteria.getMonthlyTrend(), copy.getMonthlyTrend()) &&
                condition.apply(criteria.getValidFrom(), copy.getValidFrom()) &&
                condition.apply(criteria.getValidTo(), copy.getValidTo()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
