package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TripCriteriaTest {

    @Test
    void newTripCriteriaHasAllFiltersNullTest() {
        var tripCriteria = new TripCriteria();
        assertThat(tripCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tripCriteriaFluentMethodsCreatesFiltersTest() {
        var tripCriteria = new TripCriteria();

        setAllFilters(tripCriteria);

        assertThat(tripCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tripCriteriaCopyCreatesNullFilterTest() {
        var tripCriteria = new TripCriteria();
        var copy = tripCriteria.copy();

        assertThat(tripCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tripCriteria)
        );
    }

    @Test
    void tripCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tripCriteria = new TripCriteria();
        setAllFilters(tripCriteria);

        var copy = tripCriteria.copy();

        assertThat(tripCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tripCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tripCriteria = new TripCriteria();

        assertThat(tripCriteria).hasToString("TripCriteria{}");
    }

    private static void setAllFilters(TripCriteria tripCriteria) {
        tripCriteria.id();
        tripCriteria.tripCode();
        tripCriteria.departureTime();
        tripCriteria.arrivalTime();
        tripCriteria.occasionFactor();
        tripCriteria.createdAt();
        tripCriteria.updatedAt();
        tripCriteria.isDeleted();
        tripCriteria.deletedAt();
        tripCriteria.deletedBy();
        tripCriteria.routeId();
        tripCriteria.vehicleId();
        tripCriteria.slotId();
        tripCriteria.driverId();
        tripCriteria.attendantId();
        tripCriteria.distinct();
    }

    private static Condition<TripCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTripCode()) &&
                condition.apply(criteria.getDepartureTime()) &&
                condition.apply(criteria.getArrivalTime()) &&
                condition.apply(criteria.getOccasionFactor()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getRouteId()) &&
                condition.apply(criteria.getVehicleId()) &&
                condition.apply(criteria.getSlotId()) &&
                condition.apply(criteria.getDriverId()) &&
                condition.apply(criteria.getAttendantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TripCriteria> copyFiltersAre(TripCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTripCode(), copy.getTripCode()) &&
                condition.apply(criteria.getDepartureTime(), copy.getDepartureTime()) &&
                condition.apply(criteria.getArrivalTime(), copy.getArrivalTime()) &&
                condition.apply(criteria.getOccasionFactor(), copy.getOccasionFactor()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getRouteId(), copy.getRouteId()) &&
                condition.apply(criteria.getVehicleId(), copy.getVehicleId()) &&
                condition.apply(criteria.getSlotId(), copy.getSlotId()) &&
                condition.apply(criteria.getDriverId(), copy.getDriverId()) &&
                condition.apply(criteria.getAttendantId(), copy.getAttendantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
