package com.ridehub.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A UserQuery.
 */
@Entity
@Table(name = "user_query")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 1000)
    @Column(name = "query_text", length = 1000, nullable = false)
    private String queryText;

    @NotNull
    @Column(name = "query_type", nullable = false)
    private String queryType;

    @Size(max = 1000)
    @Column(name = "parameters", length = 1000)
    private String parameters;

    @Column(name = "response_generated")
    private Boolean responseGenerated;

    @Column(name = "response_time")
    private Integer responseTime;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "deleted_by", length = 36)
    private UUID deletedBy;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "messages", "queries", "user" }, allowSetters = true)
    private ChatSession chatSession;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserQuery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQueryText() {
        return this.queryText;
    }

    public UserQuery queryText(String queryText) {
        this.setQueryText(queryText);
        return this;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getQueryType() {
        return this.queryType;
    }

    public UserQuery queryType(String queryType) {
        this.setQueryType(queryType);
        return this;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getParameters() {
        return this.parameters;
    }

    public UserQuery parameters(String parameters) {
        this.setParameters(parameters);
        return this;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Boolean getResponseGenerated() {
        return this.responseGenerated;
    }

    public UserQuery responseGenerated(Boolean responseGenerated) {
        this.setResponseGenerated(responseGenerated);
        return this;
    }

    public void setResponseGenerated(Boolean responseGenerated) {
        this.responseGenerated = responseGenerated;
    }

    public Integer getResponseTime() {
        return this.responseTime;
    }

    public UserQuery responseTime(Integer responseTime) {
        this.setResponseTime(responseTime);
        return this;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public UserQuery createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public UserQuery updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public UserQuery isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public UserQuery deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public UserQuery deletedBy(UUID deletedBy) {
        this.setDeletedBy(deletedBy);
        return this;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public ChatSession getChatSession() {
        return this.chatSession;
    }

    public void setChatSession(ChatSession chatSession) {
        this.chatSession = chatSession;
    }

    public UserQuery chatSession(ChatSession chatSession) {
        this.setChatSession(chatSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserQuery)) {
            return false;
        }
        return getId() != null && getId().equals(((UserQuery) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserQuery{" +
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
            "}";
    }
}
