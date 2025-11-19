package com.ridehub.user.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AppUserCriteriaTest {

    @Test
    void newAppUserCriteriaHasAllFiltersNullTest() {
        var appUserCriteria = new AppUserCriteria();
        assertThat(appUserCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void appUserCriteriaFluentMethodsCreatesFiltersTest() {
        var appUserCriteria = new AppUserCriteria();

        setAllFilters(appUserCriteria);

        assertThat(appUserCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void appUserCriteriaCopyCreatesNullFilterTest() {
        var appUserCriteria = new AppUserCriteria();
        var copy = appUserCriteria.copy();

        assertThat(appUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(appUserCriteria)
        );
    }

    @Test
    void appUserCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var appUserCriteria = new AppUserCriteria();
        setAllFilters(appUserCriteria);

        var copy = appUserCriteria.copy();

        assertThat(appUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(appUserCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var appUserCriteria = new AppUserCriteria();

        assertThat(appUserCriteria).hasToString("AppUserCriteria{}");
    }

    private static void setAllFilters(AppUserCriteria appUserCriteria) {
        appUserCriteria.id();
        appUserCriteria.keycloakId();
        appUserCriteria.email();
        appUserCriteria.phoneNumber();
        appUserCriteria.firstName();
        appUserCriteria.lastName();
        appUserCriteria.dateOfBirth();
        appUserCriteria.isVerified();
        appUserCriteria.isActive();
        appUserCriteria.lastLoginAt();
        appUserCriteria.createdAt();
        appUserCriteria.updatedAt();
        appUserCriteria.isDeleted();
        appUserCriteria.deletedAt();
        appUserCriteria.deletedBy();
        appUserCriteria.profileId();
        appUserCriteria.statisticsId();
        appUserCriteria.chatSessionsId();
        appUserCriteria.recommendationsId();
        appUserCriteria.distinct();
    }

    private static Condition<AppUserCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getKeycloakId()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getFirstName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getDateOfBirth()) &&
                condition.apply(criteria.getIsVerified()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getLastLoginAt()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getProfileId()) &&
                condition.apply(criteria.getStatisticsId()) &&
                condition.apply(criteria.getChatSessionsId()) &&
                condition.apply(criteria.getRecommendationsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AppUserCriteria> copyFiltersAre(AppUserCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getKeycloakId(), copy.getKeycloakId()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getFirstName(), copy.getFirstName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getDateOfBirth(), copy.getDateOfBirth()) &&
                condition.apply(criteria.getIsVerified(), copy.getIsVerified()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getLastLoginAt(), copy.getLastLoginAt()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getProfileId(), copy.getProfileId()) &&
                condition.apply(criteria.getStatisticsId(), copy.getStatisticsId()) &&
                condition.apply(criteria.getChatSessionsId(), copy.getChatSessionsId()) &&
                condition.apply(criteria.getRecommendationsId(), copy.getRecommendationsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
