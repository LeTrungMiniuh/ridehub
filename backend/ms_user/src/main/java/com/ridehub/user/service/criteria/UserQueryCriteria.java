package com.ridehub.user.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ridehub.user.domain.UserQuery} entity. This class is used
 * in {@link com.ridehub.user.web.rest.UserQueryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-queries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserQueryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter queryText;

    private StringFilter queryType;

    private StringFilter parameters;

    private BooleanFilter responseGenerated;

    private IntegerFilter responseTime;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private BooleanFilter isDeleted;

    private InstantFilter deletedAt;

    private UUIDFilter deletedBy;

    private LongFilter chatSessionId;

    private Boolean distinct;

    public UserQueryCriteria() {}

    public UserQueryCriteria(UserQueryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.queryText = other.optionalQueryText().map(StringFilter::copy).orElse(null);
        this.queryType = other.optionalQueryType().map(StringFilter::copy).orElse(null);
        this.parameters = other.optionalParameters().map(StringFilter::copy).orElse(null);
        this.responseGenerated = other.optionalResponseGenerated().map(BooleanFilter::copy).orElse(null);
        this.responseTime = other.optionalResponseTime().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(InstantFilter::copy).orElse(null);
        this.deletedBy = other.optionalDeletedBy().map(UUIDFilter::copy).orElse(null);
        this.chatSessionId = other.optionalChatSessionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UserQueryCriteria copy() {
        return new UserQueryCriteria(this);
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

    public StringFilter getQueryText() {
        return queryText;
    }

    public Optional<StringFilter> optionalQueryText() {
        return Optional.ofNullable(queryText);
    }

    public StringFilter queryText() {
        if (queryText == null) {
            setQueryText(new StringFilter());
        }
        return queryText;
    }

    public void setQueryText(StringFilter queryText) {
        this.queryText = queryText;
    }

    public StringFilter getQueryType() {
        return queryType;
    }

    public Optional<StringFilter> optionalQueryType() {
        return Optional.ofNullable(queryType);
    }

    public StringFilter queryType() {
        if (queryType == null) {
            setQueryType(new StringFilter());
        }
        return queryType;
    }

    public void setQueryType(StringFilter queryType) {
        this.queryType = queryType;
    }

    public StringFilter getParameters() {
        return parameters;
    }

    public Optional<StringFilter> optionalParameters() {
        return Optional.ofNullable(parameters);
    }

    public StringFilter parameters() {
        if (parameters == null) {
            setParameters(new StringFilter());
        }
        return parameters;
    }

    public void setParameters(StringFilter parameters) {
        this.parameters = parameters;
    }

    public BooleanFilter getResponseGenerated() {
        return responseGenerated;
    }

    public Optional<BooleanFilter> optionalResponseGenerated() {
        return Optional.ofNullable(responseGenerated);
    }

    public BooleanFilter responseGenerated() {
        if (responseGenerated == null) {
            setResponseGenerated(new BooleanFilter());
        }
        return responseGenerated;
    }

    public void setResponseGenerated(BooleanFilter responseGenerated) {
        this.responseGenerated = responseGenerated;
    }

    public IntegerFilter getResponseTime() {
        return responseTime;
    }

    public Optional<IntegerFilter> optionalResponseTime() {
        return Optional.ofNullable(responseTime);
    }

    public IntegerFilter responseTime() {
        if (responseTime == null) {
            setResponseTime(new IntegerFilter());
        }
        return responseTime;
    }

    public void setResponseTime(IntegerFilter responseTime) {
        this.responseTime = responseTime;
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
        final UserQueryCriteria that = (UserQueryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(queryText, that.queryText) &&
            Objects.equals(queryType, that.queryType) &&
            Objects.equals(parameters, that.parameters) &&
            Objects.equals(responseGenerated, that.responseGenerated) &&
            Objects.equals(responseTime, that.responseTime) &&
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
            queryText,
            queryType,
            parameters,
            responseGenerated,
            responseTime,
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
        return "UserQueryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQueryText().map(f -> "queryText=" + f + ", ").orElse("") +
            optionalQueryType().map(f -> "queryType=" + f + ", ").orElse("") +
            optionalParameters().map(f -> "parameters=" + f + ", ").orElse("") +
            optionalResponseGenerated().map(f -> "responseGenerated=" + f + ", ").orElse("") +
            optionalResponseTime().map(f -> "responseTime=" + f + ", ").orElse("") +
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
