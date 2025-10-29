package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ScheduleOccasionCriteriaTest {

    @Test
    void newScheduleOccasionCriteriaHasAllFiltersNullTest() {
        var scheduleOccasionCriteria = new ScheduleOccasionCriteria();
        assertThat(scheduleOccasionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void scheduleOccasionCriteriaFluentMethodsCreatesFiltersTest() {
        var scheduleOccasionCriteria = new ScheduleOccasionCriteria();

        setAllFilters(scheduleOccasionCriteria);

        assertThat(scheduleOccasionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void scheduleOccasionCriteriaCopyCreatesNullFilterTest() {
        var scheduleOccasionCriteria = new ScheduleOccasionCriteria();
        var copy = scheduleOccasionCriteria.copy();

        assertThat(scheduleOccasionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleOccasionCriteria)
        );
    }

    @Test
    void scheduleOccasionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var scheduleOccasionCriteria = new ScheduleOccasionCriteria();
        setAllFilters(scheduleOccasionCriteria);

        var copy = scheduleOccasionCriteria.copy();

        assertThat(scheduleOccasionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleOccasionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var scheduleOccasionCriteria = new ScheduleOccasionCriteria();

        assertThat(scheduleOccasionCriteria).hasToString("ScheduleOccasionCriteria{}");
    }

    private static void setAllFilters(ScheduleOccasionCriteria scheduleOccasionCriteria) {
        scheduleOccasionCriteria.id();
        scheduleOccasionCriteria.occasion();
        scheduleOccasionCriteria.occasionFactor();
        scheduleOccasionCriteria.createdAt();
        scheduleOccasionCriteria.updatedAt();
        scheduleOccasionCriteria.isDeleted();
        scheduleOccasionCriteria.deletedAt();
        scheduleOccasionCriteria.deletedBy();
        scheduleOccasionCriteria.scheduleId();
        scheduleOccasionCriteria.distinct();
    }

    private static Condition<ScheduleOccasionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOccasion()) &&
                condition.apply(criteria.getOccasionFactor()) &&
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

    private static Condition<ScheduleOccasionCriteria> copyFiltersAre(
        ScheduleOccasionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOccasion(), copy.getOccasion()) &&
                condition.apply(criteria.getOccasionFactor(), copy.getOccasionFactor()) &&
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
