package com.ridehub.route.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PricingTemplateCriteriaTest {

    @Test
    void newPricingTemplateCriteriaHasAllFiltersNullTest() {
        var pricingTemplateCriteria = new PricingTemplateCriteria();
        assertThat(pricingTemplateCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void pricingTemplateCriteriaFluentMethodsCreatesFiltersTest() {
        var pricingTemplateCriteria = new PricingTemplateCriteria();

        setAllFilters(pricingTemplateCriteria);

        assertThat(pricingTemplateCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void pricingTemplateCriteriaCopyCreatesNullFilterTest() {
        var pricingTemplateCriteria = new PricingTemplateCriteria();
        var copy = pricingTemplateCriteria.copy();

        assertThat(pricingTemplateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(pricingTemplateCriteria)
        );
    }

    @Test
    void pricingTemplateCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var pricingTemplateCriteria = new PricingTemplateCriteria();
        setAllFilters(pricingTemplateCriteria);

        var copy = pricingTemplateCriteria.copy();

        assertThat(pricingTemplateCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(pricingTemplateCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var pricingTemplateCriteria = new PricingTemplateCriteria();

        assertThat(pricingTemplateCriteria).hasToString("PricingTemplateCriteria{}");
    }

    private static void setAllFilters(PricingTemplateCriteria pricingTemplateCriteria) {
        pricingTemplateCriteria.id();
        pricingTemplateCriteria.vehicleType();
        pricingTemplateCriteria.seatType();
        pricingTemplateCriteria.occasionType();
        pricingTemplateCriteria.baseFare();
        pricingTemplateCriteria.vehicleFactor();
        pricingTemplateCriteria.floorFactor();
        pricingTemplateCriteria.seatFactor();
        pricingTemplateCriteria.occasionFactor();
        pricingTemplateCriteria.finalPrice();
        pricingTemplateCriteria.validFrom();
        pricingTemplateCriteria.validTo();
        pricingTemplateCriteria.createdAt();
        pricingTemplateCriteria.updatedAt();
        pricingTemplateCriteria.isDeleted();
        pricingTemplateCriteria.deletedAt();
        pricingTemplateCriteria.deletedBy();
        pricingTemplateCriteria.routeId();
        pricingTemplateCriteria.distinct();
    }

    private static Condition<PricingTemplateCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getVehicleType()) &&
                condition.apply(criteria.getSeatType()) &&
                condition.apply(criteria.getOccasionType()) &&
                condition.apply(criteria.getBaseFare()) &&
                condition.apply(criteria.getVehicleFactor()) &&
                condition.apply(criteria.getFloorFactor()) &&
                condition.apply(criteria.getSeatFactor()) &&
                condition.apply(criteria.getOccasionFactor()) &&
                condition.apply(criteria.getFinalPrice()) &&
                condition.apply(criteria.getValidFrom()) &&
                condition.apply(criteria.getValidTo()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getRouteId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PricingTemplateCriteria> copyFiltersAre(
        PricingTemplateCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getVehicleType(), copy.getVehicleType()) &&
                condition.apply(criteria.getSeatType(), copy.getSeatType()) &&
                condition.apply(criteria.getOccasionType(), copy.getOccasionType()) &&
                condition.apply(criteria.getBaseFare(), copy.getBaseFare()) &&
                condition.apply(criteria.getVehicleFactor(), copy.getVehicleFactor()) &&
                condition.apply(criteria.getFloorFactor(), copy.getFloorFactor()) &&
                condition.apply(criteria.getSeatFactor(), copy.getSeatFactor()) &&
                condition.apply(criteria.getOccasionFactor(), copy.getOccasionFactor()) &&
                condition.apply(criteria.getFinalPrice(), copy.getFinalPrice()) &&
                condition.apply(criteria.getValidFrom(), copy.getValidFrom()) &&
                condition.apply(criteria.getValidTo(), copy.getValidTo()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getRouteId(), copy.getRouteId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
