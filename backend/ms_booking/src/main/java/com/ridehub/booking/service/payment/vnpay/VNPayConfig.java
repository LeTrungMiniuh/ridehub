package com.ridehub.booking.service.payment.vnpay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * VNPay configuration properties
 */
@Component
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {
    private String returnFEURL;
    private String tmnCode;
    private String hashSecret;
    private String payUrl;
    private String queryUrl;
    private String returnUrl;
    private String version = "2.1.0";
    private String command = "pay";
    private String orderType = "other";
    private String locale = "vn";
    private String currCode = "VND";
    private String refundUrl;
    private String refundTransactionTypeFull = "02";
    private String refundTransactionTypePartial = "03";
    private String refundCreateBy = "system";

    // Getters and Setters
    public String getTmnCode() {
        return tmnCode;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public void setHashSecret(String hashSecret) {
        this.hashSecret = hashSecret;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCurrCode() {
        return currCode;
    }

    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }

    public String getRefundUrl() {
        return refundUrl;
    }

    public void setRefundUrl(String refundUrl) {
        this.refundUrl = refundUrl;
    }

    public String getRefundTransactionTypeFull() {
        return refundTransactionTypeFull;
    }

    public void setRefundTransactionTypeFull(String refundTransactionTypeFull) {
        this.refundTransactionTypeFull = refundTransactionTypeFull;
    }

    public String getRefundTransactionTypePartial() {
        return refundTransactionTypePartial;
    }

    public void setRefundTransactionTypePartial(String refundTransactionTypePartial) {
        this.refundTransactionTypePartial = refundTransactionTypePartial;
    }

    public String getRefundCreateBy() {
        return refundCreateBy;
    }

    public void setRefundCreateBy(String refundCreateBy) {
        this.refundCreateBy = refundCreateBy;
    }

    public String getReturnFEURL() {
        return returnFEURL;
    }

    public void setReturnFEURL(String returnFEURL) {
        this.returnFEURL = returnFEURL;
    }
}
