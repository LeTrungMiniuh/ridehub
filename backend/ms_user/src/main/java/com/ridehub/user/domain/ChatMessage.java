package com.ridehub.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A ChatMessage.
 */
@Entity
@Table(name = "chat_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 1000)
    @Column(name = "message_text", length = 1000, nullable = false)
    private String messageText;

    @NotNull
    @Column(name = "message_type", nullable = false)
    private String messageType;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Size(max = 100)
    @Column(name = "intent", length = 100)
    private String intent;

    @Size(max = 500)
    @Column(name = "entities", length = 500)
    private String entities;

    @Column(name = "confidence", precision = 21, scale = 2)
    private BigDecimal confidence;

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

    public ChatMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public ChatMessage messageText(String messageText) {
        this.setMessageText(messageText);
        return this;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public ChatMessage messageType(String messageType) {
        this.setMessageType(messageType);
        return this;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public ChatMessage timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getIntent() {
        return this.intent;
    }

    public ChatMessage intent(String intent) {
        this.setIntent(intent);
        return this;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getEntities() {
        return this.entities;
    }

    public ChatMessage entities(String entities) {
        this.setEntities(entities);
        return this;
    }

    public void setEntities(String entities) {
        this.entities = entities;
    }

    public BigDecimal getConfidence() {
        return this.confidence;
    }

    public ChatMessage confidence(BigDecimal confidence) {
        this.setConfidence(confidence);
        return this;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ChatMessage createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public ChatMessage updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public ChatMessage isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getDeletedAt() {
        return this.deletedAt;
    }

    public ChatMessage deletedAt(Instant deletedAt) {
        this.setDeletedAt(deletedAt);
        return this;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return this.deletedBy;
    }

    public ChatMessage deletedBy(UUID deletedBy) {
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

    public ChatMessage chatSession(ChatSession chatSession) {
        this.setChatSession(chatSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((ChatMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatMessage{" +
            "id=" + getId() +
            ", messageText='" + getMessageText() + "'" +
            ", messageType='" + getMessageType() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", intent='" + getIntent() + "'" +
            ", entities='" + getEntities() + "'" +
            ", confidence=" + getConfidence() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            ", deletedBy='" + getDeletedBy() + "'" +
            "}";
    }
}
