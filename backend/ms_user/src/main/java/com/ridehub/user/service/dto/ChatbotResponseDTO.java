package com.ridehub.user.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatbotResponseDTO implements Serializable {

    private String sessionId;
    private String message;
    private String error;
    private Instant timestamp;
    private Boolean success;

    private ChatbotResponseDTO(Builder builder) {
        this.sessionId = builder.sessionId;
        this.message = builder.message;
        this.error = builder.error;
        this.timestamp = builder.timestamp;
        this.success = builder.success;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class Builder {
        private String sessionId;
        private String message;
        private String error;
        private Instant timestamp;
        private Boolean success;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder success(Boolean success) {
            this.success = success;
            return this;
        }

        public ChatbotResponseDTO build() {
            return new ChatbotResponseDTO(this);
        }
    }

    @Override
    public String toString() {
        return "ChatbotResponseDTO{" +
                "sessionId='" + sessionId + '\'' +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", timestamp=" + timestamp +
                ", success=" + success +
                '}';
    }
}