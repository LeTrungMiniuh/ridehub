package com.ridehub.user.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UserQueryCriteriaTest {

    @Test
    void newUserQueryCriteriaHasAllFiltersNullTest() {
        var userQueryCriteria = new UserQueryCriteria();
        assertThat(userQueryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void userQueryCriteriaFluentMethodsCreatesFiltersTest() {
        var userQueryCriteria = new UserQueryCriteria();

        setAllFilters(userQueryCriteria);

        assertThat(userQueryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void userQueryCriteriaCopyCreatesNullFilterTest() {
        var userQueryCriteria = new UserQueryCriteria();
        var copy = userQueryCriteria.copy();

        assertThat(userQueryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(userQueryCriteria)
        );
    }

    @Test
    void userQueryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var userQueryCriteria = new UserQueryCriteria();
        setAllFilters(userQueryCriteria);

        var copy = userQueryCriteria.copy();

        assertThat(userQueryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(userQueryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var userQueryCriteria = new UserQueryCriteria();

        assertThat(userQueryCriteria).hasToString("UserQueryCriteria{}");
    }

    private static void setAllFilters(UserQueryCriteria userQueryCriteria) {
        userQueryCriteria.id();
        userQueryCriteria.queryText();
        userQueryCriteria.queryType();
        userQueryCriteria.parameters();
        userQueryCriteria.responseGenerated();
        userQueryCriteria.responseTime();
        userQueryCriteria.createdAt();
        userQueryCriteria.updatedAt();
        userQueryCriteria.isDeleted();
        userQueryCriteria.deletedAt();
        userQueryCriteria.deletedBy();
        userQueryCriteria.chatSessionId();
        userQueryCriteria.distinct();
    }

    private static Condition<UserQueryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQueryText()) &&
                condition.apply(criteria.getQueryType()) &&
                condition.apply(criteria.getParameters()) &&
                condition.apply(criteria.getResponseGenerated()) &&
                condition.apply(criteria.getResponseTime()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getChatSessionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UserQueryCriteria> copyFiltersAre(UserQueryCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQueryText(), copy.getQueryText()) &&
                condition.apply(criteria.getQueryType(), copy.getQueryType()) &&
                condition.apply(criteria.getParameters(), copy.getParameters()) &&
                condition.apply(criteria.getResponseGenerated(), copy.getResponseGenerated()) &&
                condition.apply(criteria.getResponseTime(), copy.getResponseTime()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getChatSessionId(), copy.getChatSessionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
