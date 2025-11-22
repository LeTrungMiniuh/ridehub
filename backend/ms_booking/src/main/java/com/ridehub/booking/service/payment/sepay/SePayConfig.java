package com.ridehub.booking.service.payment.sepay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SePay configuration properties
 */
@Component
@ConfigurationProperties(prefix = "sepay")
public class SePayConfig {

    private String merchantId;
    private String secretKey;
    private String apiKey;
    private String apiBaseUrl = "https://pgapi-sandbox.sepay.vn";
    private String initUrl = "https://pay-sandbox.sepay.vn/v1/checkout/init";
    private String successUrl;
    private String errorUrl;
    private String cancelUrl;
    private String returnFEURL;

    // Timeout configurations
    private int connectTimeoutSeconds = 30;
    private int requestTimeoutSeconds = 45;
    private int maxRetries = 3;
    private int retryDelayMillis = 2000;

    // Getters and Setters
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getInitUrl() {
        return initUrl;
    }

    public void setInitUrl(String initUrl) {
        this.initUrl = initUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public int getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public void setRequestTimeoutSeconds(int requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public void setRetryDelayMillis(int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
    }

    public String getReturnFEURL() {
        return returnFEURL;
    }

    public void setReturnFEURL(String returnFEURL) {
        this.returnFEURL = returnFEURL;
    }
}