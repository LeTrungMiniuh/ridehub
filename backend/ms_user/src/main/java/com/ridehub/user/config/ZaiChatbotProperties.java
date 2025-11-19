package com.ridehub.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zai.chatbot")
public class ZaiChatbotProperties {

    private String apiKey;
    private String baseUrl = "https://api.z.ai/api/paas/v4/";
    private String model = "glm-4.6";
    private Float temperature = 1.0f;
    private Integer maxTokens = 1024;
    private Boolean enableTokenCache = true;
    private Long tokenExpire = 3600000L; // 1 hour
    private Integer connectionPoolSize = 10;
    private Long connectionPoolKeepAlive = 300000L; // 5 minutes

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Boolean getEnableTokenCache() {
        return enableTokenCache;
    }

    public void setEnableTokenCache(Boolean enableTokenCache) {
        this.enableTokenCache = enableTokenCache;
    }

    public Long getTokenExpire() {
        return tokenExpire;
    }

    public void setTokenExpire(Long tokenExpire) {
        this.tokenExpire = tokenExpire;
    }

    public Integer getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(Integer connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public Long getConnectionPoolKeepAlive() {
        return connectionPoolKeepAlive;
    }

    public void setConnectionPoolKeepAlive(Long connectionPoolKeepAlive) {
        this.connectionPoolKeepAlive = connectionPoolKeepAlive;
    }
}