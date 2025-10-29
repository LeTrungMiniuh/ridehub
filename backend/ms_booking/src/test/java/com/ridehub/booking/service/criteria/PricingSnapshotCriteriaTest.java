package com.ridehub.booking.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PricingSnapshotCriteriaTest {

    @Test
    void newPricingSnapshotCriteriaHasAllFiltersNullTest() {
        var pricingSnapshotCriteria = new PricingSnapshotCriteria();
        assertThat(pricingSnapshotCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void pricingSnapshotCriteriaFluentMethodsCreatesFiltersTest() {
        var pricingSnapshotCriteria = new PricingSnapshotCriteria();

        setAllFilters(pricingSnapshotCriteria);

        assertThat(pricingSnapshotCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void pricingSnapshotCriteriaCopyCreatesNullFilterTest() {
        var pricingSnapshotCriteria = new PricingSnapshotCriteria();
        var copy = pricingSnapshotCriteria.copy();

        assertThat(pricingSnapshotCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(pricingSnapshotCriteria)
        );
    }

    @Test
    void pricingSnapshotCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var pricingSnapshotCriteria = new PricingSnapshotCriteria();
        setAllFilters(pricingSnapshotCriteria);

        var copy = pricingSnapshotCriteria.copy();

        assertThat(pricingSnapshotCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(pricingSnapshotCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var pricingSnapshotCriteria = new PricingSnapshotCriteria();

        assertThat(pricingSnapshotCriteria).hasToString("PricingSnapshotCriteria{}");
    }

    private static void setAllFilters(PricingSnapshotCriteria pricingSnapshotCriteria) {
        pricingSnapshotCriteria.id();
        pricingSnapshotCriteria.baseFare();
        pricingSnapshotCriteria.vehicleFactor();
        pricingSnapshotCriteria.floorFactor();
        pricingSnapshotCriteria.seatFactor();
        pricingSnapshotCriteria.scheduleOccasionFactor();
        pricingSnapshotCriteria.finalPrice();
        pricingSnapshotCriteria.createdAt();
        pricingSnapshotCriteria.updatedAt();
        pricingSnapshotCriteria.isDeleted();
        pricingSnapshotCriteria.deletedAt();
        pricingSnapshotCriteria.deletedBy();
        pricingSnapshotCriteria.bookingId();
        pricingSnapshotCriteria.distinct();
    }

    private static Condition<PricingSnapshotCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBaseFare()) &&
                condition.apply(criteria.getVehicleFactor()) &&
                condition.apply(criteria.getFloorFactor()) &&
                condition.apply(criteria.getSeatFactor()) &&
                condition.apply(criteria.getScheduleOccasionFactor()) &&
                condition.apply(criteria.getFinalPrice()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getBookingId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PricingSnapshotCriteria> copyFiltersAre(
        PricingSnapshotCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBaseFare(), copy.getBaseFare()) &&
                condition.apply(criteria.getVehicleFactor(), copy.getVehicleFactor()) &&
                condition.apply(criteria.getFloorFactor(), copy.getFloorFactor()) &&
                condition.apply(criteria.getSeatFactor(), copy.getSeatFactor()) &&
                condition.apply(criteria.getScheduleOccasionFactor(), copy.getScheduleOccasionFactor()) &&
                condition.apply(criteria.getFinalPrice(), copy.getFinalPrice()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getBookingId(), copy.getBookingId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
