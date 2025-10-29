package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ScheduleCriteriaTest {

    @Test
    void newScheduleCriteriaHasAllFiltersNullTest() {
        var scheduleCriteria = new ScheduleCriteria();
        assertThat(scheduleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void scheduleCriteriaFluentMethodsCreatesFiltersTest() {
        var scheduleCriteria = new ScheduleCriteria();

        setAllFilters(scheduleCriteria);

        assertThat(scheduleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void scheduleCriteriaCopyCreatesNullFilterTest() {
        var scheduleCriteria = new ScheduleCriteria();
        var copy = scheduleCriteria.copy();

        assertThat(scheduleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleCriteria)
        );
    }

    @Test
    void scheduleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var scheduleCriteria = new ScheduleCriteria();
        setAllFilters(scheduleCriteria);

        var copy = scheduleCriteria.copy();

        assertThat(scheduleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var scheduleCriteria = new ScheduleCriteria();

        assertThat(scheduleCriteria).hasToString("ScheduleCriteria{}");
    }

    private static void setAllFilters(ScheduleCriteria scheduleCriteria) {
        scheduleCriteria.id();
        scheduleCriteria.scheduleCode();
        scheduleCriteria.startDate();
        scheduleCriteria.endDate();
        scheduleCriteria.daysOfWeek();
        scheduleCriteria.active();
        scheduleCriteria.createdAt();
        scheduleCriteria.updatedAt();
        scheduleCriteria.isDeleted();
        scheduleCriteria.deletedAt();
        scheduleCriteria.deletedBy();
        scheduleCriteria.timeSlotsId();
        scheduleCriteria.occasionRuleId();
        scheduleCriteria.routeId();
        scheduleCriteria.distinct();
    }

    private static Condition<ScheduleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getScheduleCode()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getDaysOfWeek()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getTimeSlotsId()) &&
                condition.apply(criteria.getOccasionRuleId()) &&
                condition.apply(criteria.getRouteId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ScheduleCriteria> copyFiltersAre(ScheduleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getScheduleCode(), copy.getScheduleCode()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getDaysOfWeek(), copy.getDaysOfWeek()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getTimeSlotsId(), copy.getTimeSlotsId()) &&
                condition.apply(criteria.getOccasionRuleId(), copy.getOccasionRuleId()) &&
                condition.apply(criteria.getRouteId(), copy.getRouteId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
