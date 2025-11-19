package com.ridehub.user.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ChatMessageCriteriaTest {

    @Test
    void newChatMessageCriteriaHasAllFiltersNullTest() {
        var chatMessageCriteria = new ChatMessageCriteria();
        assertThat(chatMessageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void chatMessageCriteriaFluentMethodsCreatesFiltersTest() {
        var chatMessageCriteria = new ChatMessageCriteria();

        setAllFilters(chatMessageCriteria);

        assertThat(chatMessageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void chatMessageCriteriaCopyCreatesNullFilterTest() {
        var chatMessageCriteria = new ChatMessageCriteria();
        var copy = chatMessageCriteria.copy();

        assertThat(chatMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(chatMessageCriteria)
        );
    }

    @Test
    void chatMessageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var chatMessageCriteria = new ChatMessageCriteria();
        setAllFilters(chatMessageCriteria);

        var copy = chatMessageCriteria.copy();

        assertThat(chatMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(chatMessageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var chatMessageCriteria = new ChatMessageCriteria();

        assertThat(chatMessageCriteria).hasToString("ChatMessageCriteria{}");
    }

    private static void setAllFilters(ChatMessageCriteria chatMessageCriteria) {
        chatMessageCriteria.id();
        chatMessageCriteria.messageText();
        chatMessageCriteria.messageType();
        chatMessageCriteria.timestamp();
        chatMessageCriteria.intent();
        chatMessageCriteria.entities();
        chatMessageCriteria.confidence();
        chatMessageCriteria.createdAt();
        chatMessageCriteria.updatedAt();
        chatMessageCriteria.isDeleted();
        chatMessageCriteria.deletedAt();
        chatMessageCriteria.deletedBy();
        chatMessageCriteria.chatSessionId();
        chatMessageCriteria.distinct();
    }

    private static Condition<ChatMessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMessageText()) &&
                condition.apply(criteria.getMessageType()) &&
                condition.apply(criteria.getTimestamp()) &&
                condition.apply(criteria.getIntent()) &&
                condition.apply(criteria.getEntities()) &&
                condition.apply(criteria.getConfidence()) &&
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

    private static Condition<ChatMessageCriteria> copyFiltersAre(ChatMessageCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMessageText(), copy.getMessageText()) &&
                condition.apply(criteria.getMessageType(), copy.getMessageType()) &&
                condition.apply(criteria.getTimestamp(), copy.getTimestamp()) &&
                condition.apply(criteria.getIntent(), copy.getIntent()) &&
                condition.apply(criteria.getEntities(), copy.getEntities()) &&
                condition.apply(criteria.getConfidence(), copy.getConfidence()) &&
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
