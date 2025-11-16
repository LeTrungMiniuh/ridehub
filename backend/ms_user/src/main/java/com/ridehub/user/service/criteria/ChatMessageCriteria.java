package com.ridehub.user.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.user.domain.ChatMessage} entity. This class is used
 * in {@link com.ridehub.user.web.rest.ChatMessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /chat-messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatMessageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter messageText;

    private StringFilter messageType;

    private InstantFilter timestamp;

    private StringFilter intent;

    private StringFilter entities;

    private BigDecimalFilter confidence;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter chatSessionId;

    private Boolean distinct;

    public ChatMessageCriteria() {}

    public ChatMessageCriteria(ChatMessageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.messageText = other.optionalMessageText().map(StringFilter::copy).orElse(null);
        this.messageType = other.optionalMessageType().map(StringFilter::copy).orElse(null);
        this.timestamp = other.optionalTimestamp().map(InstantFilter::copy).orElse(null);
        this.intent = other.optionalIntent().map(StringFilter::copy).orElse(null);
        this.entities = other.optionalEntities().map(StringFilter::copy).orElse(null);
        this.confidence = other.optionalConfidence().map(BigDecimalFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.chatSessionId = other.optionalChatSessionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ChatMessageCriteria copy() {
        return new ChatMessageCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getMessageText() {
        return messageText;
    }

    public Optional<StringFilter> optionalMessageText() {
        return Optional.ofNullable(messageText);
    }

    public StringFilter messageText() {
        if (messageText == null) {
            setMessageText(new StringFilter());
        }
        return messageText;
    }

    public void setMessageText(StringFilter messageText) {
        this.messageText = messageText;
    }

    public StringFilter getMessageType() {
        return messageType;
    }

    public Optional<StringFilter> optionalMessageType() {
        return Optional.ofNullable(messageType);
    }

    public StringFilter messageType() {
        if (messageType == null) {
            setMessageType(new StringFilter());
        }
        return messageType;
    }

    public void setMessageType(StringFilter messageType) {
        this.messageType = messageType;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public Optional<InstantFilter> optionalTimestamp() {
        return Optional.ofNullable(timestamp);
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            setTimestamp(new InstantFilter());
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
    }

    public StringFilter getIntent() {
        return intent;
    }

    public Optional<StringFilter> optionalIntent() {
        return Optional.ofNullable(intent);
    }

    public StringFilter intent() {
        if (intent == null) {
            setIntent(new StringFilter());
        }
        return intent;
    }

    public void setIntent(StringFilter intent) {
        this.intent = intent;
    }

    public StringFilter getEntities() {
        return entities;
    }

    public Optional<StringFilter> optionalEntities() {
        return Optional.ofNullable(entities);
    }

    public StringFilter entities() {
        if (entities == null) {
            setEntities(new StringFilter());
        }
        return entities;
    }

    public void setEntities(StringFilter entities) {
        this.entities = entities;
    }

    public BigDecimalFilter getConfidence() {
        return confidence;
    }

    public Optional<BigDecimalFilter> optionalConfidence() {
        return Optional.ofNullable(confidence);
    }

    public BigDecimalFilter confidence() {
        if (confidence == null) {
            setConfidence(new BigDecimalFilter());
        }
        return confidence;
    }

    public void setConfidence(BigDecimalFilter confidence) {
        this.confidence = confidence;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public InstantFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<InstantFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public InstantFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new InstantFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUIDFilter getDeletedBy() {
        return deletedBy;
    }

    public Optional<UUIDFilter> optionalDeletedBy() {
        return Optional.ofNullable(deletedBy);
    }

    public UUIDFilter deletedBy() {
        if (deletedBy == null) {
            setDeletedBy(new UUIDFilter());
        }
        return deletedBy;
    }

    public void setDeletedBy(UUIDFilter deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LongFilter getChatSessionId() {
        return chatSessionId;
    }

    public Optional<LongFilter> optionalChatSessionId() {
        return Optional.ofNullable(chatSessionId);
    }

    public LongFilter chatSessionId() {
        if (chatSessionId == null) {
            setChatSessionId(new LongFilter());
        }
        return chatSessionId;
    }

    public void setChatSessionId(LongFilter chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ChatMessageCriteria that = (ChatMessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(messageText, that.messageText) &&
            Objects.equals(messageType, that.messageType) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(intent, that.intent) &&
            Objects.equals(entities, that.entities) &&
            Objects.equals(confidence, that.confidence) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(deletedBy, that.deletedBy) &&
            Objects.equals(chatSessionId, that.chatSessionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            messageText,
            messageType,
            timestamp,
            intent,
            entities,
            confidence,
            createdAt,
            updatedAt,
            isDeleted,
            deletedAt,
            deletedBy,
            chatSessionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatMessageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMessageText().map(f -> "messageText=" + f + ", ").orElse("") +
            optionalMessageType().map(f -> "messageType=" + f + ", ").orElse("") +
            optionalTimestamp().map(f -> "timestamp=" + f + ", ").orElse("") +
            optionalIntent().map(f -> "intent=" + f + ", ").orElse("") +
            optionalEntities().map(f -> "entities=" + f + ", ").orElse("") +
            optionalConfidence().map(f -> "confidence=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDeletedBy().map(f -> "deletedBy=" + f + ", ").orElse("") +
            optionalChatSessionId().map(f -> "chatSessionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
