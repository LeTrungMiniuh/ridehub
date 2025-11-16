package com.ridehub.user.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ChatSessionCriteriaTest {

    @Test
    void newChatSessionCriteriaHasAllFiltersNullTest() {
        var chatSessionCriteria = new ChatSessionCriteria();
        assertThat(chatSessionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void chatSessionCriteriaFluentMethodsCreatesFiltersTest() {
        var chatSessionCriteria = new ChatSessionCriteria();

        setAllFilters(chatSessionCriteria);

        assertThat(chatSessionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void chatSessionCriteriaCopyCreatesNullFilterTest() {
        var chatSessionCriteria = new ChatSessionCriteria();
        var copy = chatSessionCriteria.copy();

        assertThat(chatSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(chatSessionCriteria)
        );
    }

    @Test
    void chatSessionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var chatSessionCriteria = new ChatSessionCriteria();
        setAllFilters(chatSessionCriteria);

        var copy = chatSessionCriteria.copy();

        assertThat(chatSessionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(chatSessionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var chatSessionCriteria = new ChatSessionCriteria();

        assertThat(chatSessionCriteria).hasToString("ChatSessionCriteria{}");
    }

    private static void setAllFilters(ChatSessionCriteria chatSessionCriteria) {
        chatSessionCriteria.id();
        chatSessionCriteria.sessionId();
        chatSessionCriteria.startedAt();
        chatSessionCriteria.endedAt();
        chatSessionCriteria.isActive();
        chatSessionCriteria.context();
        chatSessionCriteria.createdAt();
        chatSessionCriteria.updatedAt();
        chatSessionCriteria.isDeleted();
        chatSessionCriteria.deletedAt();
        chatSessionCriteria.deletedBy();
        chatSessionCriteria.messagesId();
        chatSessionCriteria.queriesId();
        chatSessionCriteria.userId();
        chatSessionCriteria.distinct();
    }

    private static Condition<ChatSessionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSessionId()) &&
                condition.apply(criteria.getStartedAt()) &&
                condition.apply(criteria.getEndedAt()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getContext()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy()) &&
                condition.apply(criteria.getMessagesId()) &&
                condition.apply(criteria.getQueriesId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ChatSessionCriteria> copyFiltersAre(ChatSessionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSessionId(), copy.getSessionId()) &&
                condition.apply(criteria.getStartedAt(), copy.getStartedAt()) &&
                condition.apply(criteria.getEndedAt(), copy.getEndedAt()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getContext(), copy.getContext()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getIsDeleted(), copy.getIsDeleted()) &&
                condition.apply(criteria.getDeletedAt(), copy.getDeletedAt()) &&
                condition.apply(criteria.getDeletedBy(), copy.getDeletedBy()) &&
                condition.apply(criteria.getMessagesId(), copy.getMessagesId()) &&
                condition.apply(criteria.getQueriesId(), copy.getQueriesId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
