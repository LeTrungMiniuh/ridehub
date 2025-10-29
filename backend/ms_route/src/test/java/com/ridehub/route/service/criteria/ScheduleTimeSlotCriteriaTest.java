package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ScheduleTimeSlotCriteriaTest {

    @Test
    void newScheduleTimeSlotCriteriaHasAllFiltersNullTest() {
        var scheduleTimeSlotCriteria = new ScheduleTimeSlotCriteria();
        assertThat(scheduleTimeSlotCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void scheduleTimeSlotCriteriaFluentMethodsCreatesFiltersTest() {
        var scheduleTimeSlotCriteria = new ScheduleTimeSlotCriteria();

        setAllFilters(scheduleTimeSlotCriteria);

        assertThat(scheduleTimeSlotCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void scheduleTimeSlotCriteriaCopyCreatesNullFilterTest() {
        var scheduleTimeSlotCriteria = new ScheduleTimeSlotCriteria();
        var copy = scheduleTimeSlotCriteria.copy();

        assertThat(scheduleTimeSlotCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleTimeSlotCriteria)
        );
    }

    @Test
    void scheduleTimeSlotCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var scheduleTimeSlotCriteria = new ScheduleTimeSlotCriteria();
        setAllFilters(scheduleTimeSlotCriteria);

        var copy = scheduleTimeSlotCriteria.copy();

        assertThat(scheduleTimeSlotCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleTimeSlotCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var scheduleTimeSlotCriteria = new ScheduleTimeSlotCriteria();

        assertThat(scheduleTimeSlotCriteria).hasToString("ScheduleTimeSlotCriteria{}");
    }

    private static void setAllFilters(ScheduleTimeSlotCriteria scheduleTimeSlotCriteria) {
        scheduleTimeSlotCriteria.id();
        scheduleTimeSlotCriteria.slotCode();
        scheduleTimeSlotCriteria.departureTime();
        scheduleTimeSlotCriteria.arrivalTime();
        scheduleTimeSlotCriteria.bufferMinutes();
        scheduleTimeSlotCriteria.sequence();
        scheduleTimeSlotCriteria.active();
        scheduleTimeSlotCriteria.createdAt();
        scheduleTimeSlotCriteria.updatedAt();
        scheduleTimeSlotCriteria.isDeleted();
        scheduleTimeSlotCriteria.deletedAt();
        scheduleTimeSlotCriteria.deletedBy();
        scheduleTimeSlotCriteria.scheduleId();
        scheduleTimeSlotCriteria.distinct();
    }

    private static Condition<ScheduleTimeSlotCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSlotCode()) &&
                condition.apply(criteria.getDepartureTime()) &&
                condition.apply(criteria.getArrivalTime()) &&
                condition.apply(criteria.getBufferMinutes()) &&
                condition.apply(criteria.getSequence()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getScheduleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ScheduleTimeSlotCriteria> copyFiltersAre(
        ScheduleTimeSlotCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSlotCode(), copy.getSlotCode()) &&
                condition.apply(criteria.getDepartureTime(), copy.getDepartureTime()) &&
                condition.apply(criteria.getArrivalTime(), copy.getArrivalTime()) &&
                condition.apply(criteria.getBufferMinutes(), copy.getBufferMinutes()) &&
                condition.apply(criteria.getSequence(), copy.getSequence()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getScheduleId(), copy.getScheduleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
