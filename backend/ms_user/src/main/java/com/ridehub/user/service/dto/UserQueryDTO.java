package com.ridehub.user.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.ridehub.user.domain.UserQuery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserQueryDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 1000)
    private String queryText;

    @NotNull
    private String queryType;

    @Size(max = 1000)
    private String parameters;

    private Boolean responseGenerated;

    private Integer responseTime;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isDeleted;

    private Instant deletedAt;

    private UUID deletedBy;

    @NotNull
    private ChatSessionDTO chatSession;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Boolean getResponseGenerated() {
        return responseGenerated;
    }

    public void setResponseGenerated(Boolean responseGenerated) {
        this.responseGenerated = responseGenerated;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public ChatSessionDTO getChatSession() {
        return chatSession;
    }

    public void setChatSession(ChatSessionDTO chatSession) {
        this.chatSession = chatSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserQueryDTO)) {
            return false;
        }

        UserQueryDTO userQueryDTO = (UserQueryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userQueryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserQueryDTO{" +
            "id=" + getId() +
            ", queryText='" + getQueryText() + "'" +
            ", queryType='" + getQueryType() + "'" +
            ", parameters='" + getParameters() + "'" +
            ", responseGenerated='" + getResponseGenerated() + "'" +
            ", responseTime=" + getResponseTime() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            ", chatSession=" + getChatSession() +
            "}";
    }
}
