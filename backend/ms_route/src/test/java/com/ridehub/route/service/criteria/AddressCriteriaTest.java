package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AddressCriteriaTest {

    @Test
    void newAddressCriteriaHasAllFiltersNullTest() {
        var addressCriteria = new AddressCriteria();
        assertThat(addressCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void addressCriteriaFluentMethodsCreatesFiltersTest() {
        var addressCriteria = new AddressCriteria();

        setAllFilters(addressCriteria);

        assertThat(addressCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void addressCriteriaCopyCreatesNullFilterTest() {
        var addressCriteria = new AddressCriteria();
        var copy = addressCriteria.copy();

        assertThat(addressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(addressCriteria)
        );
    }

    @Test
    void addressCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var addressCriteria = new AddressCriteria();
        setAllFilters(addressCriteria);

        var copy = addressCriteria.copy();

        assertThat(addressCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(addressCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var addressCriteria = new AddressCriteria();

        assertThat(addressCriteria).hasToString("AddressCriteria{}");
    }

    private static void setAllFilters(AddressCriteria addressCriteria) {
        addressCriteria.id();
        addressCriteria.streetAddress();
        addressCriteria.latitude();
        addressCriteria.longitude();
        addressCriteria.createdAt();
        addressCriteria.updatedAt();
        addressCriteria.isDeleted();
        addressCriteria.deletedAt();
        addressCriteria.deletedBy();
        addressCriteria.wardId();
        addressCriteria.distinct();
    }

    private static Condition<AddressCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStreetAddress()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getWardId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AddressCriteria> copyFiltersAre(AddressCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStreetAddress(), copy.getStreetAddress()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getWardId(), copy.getWardId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
