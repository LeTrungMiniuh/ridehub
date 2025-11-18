package com.ridehub.booking.service.payment.sepay;

import com.ridehub.booking.domain.PaymentTransaction;
import com.ridehub.booking.service.vm.InitiatePaymentRequestVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * SePay payment service implementation
 */
@Service
public class SePayService {

    private static final Logger LOG = LoggerFactory.getLogger(SePayService.class);

    private final SePayConfig sePayConfig;
    private final RestTemplate restTemplate;

    public SePayService(SePayConfig sePayConfig) {
        this.sePayConfig = sePayConfig;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Create SePay payment URL
     */
    public String createPaymentUrl(InitiatePaymentRequestVM request, String transactionId,
            String orderRef, BigDecimal amount, String returnUrl, String ipAddress, Instant bookingExpiresAt) {
        LOG.debug("Creating SePay payment URL for transaction: {}", transactionId);

        try {
            String checkoutUrl = createCheckoutUrl(amount, orderRef);
            LOG.debug("SePay payment URL created successfully for transaction: {}", transactionId);
            return checkoutUrl;
        } catch (Exception e) {
            LOG.error("Error creating SePay payment URL for transaction: {}", transactionId, e);
            throw new RuntimeException("Failed to create SePay payment URL", e);
        }
    }

    /**
     * Verify SePay callback/webhook
     */
    public SePayCallbackResult verifyCallback(Map<String, String> params) {
        LOG.debug("Verifying SePay callback");

        // Validate signature
        if (!validateSignature(params)) {
            LOG.warn("Invalid SePay callback signature");
            return new SePayCallbackResult(false, null, null, "Invalid signature");
        }

        String transactionId = params.get("order_invoice_number");
        String status = params.get("status");
        String responseCode = params.get("response_code");

        boolean isSuccess = "00".equals(responseCode) && "SUCCESS".equalsIgnoreCase(status);
        String paymentStatus = isSuccess ? "SUCCESS" : "FAILED";

        LOG.debug("SePay callback verified - Status: {}, Transaction: {}", paymentStatus, transactionId);

        return new SePayCallbackResult(true, transactionId, paymentStatus,
                isSuccess ? "Payment successful" : "Payment failed: " + responseCode);
    }

    /**
     * Parse SePay webhook payload
     */
    public SePayWebhookData parseWebhookPayload(String payload) {
        LOG.debug("Parsing SePay webhook payload");

        Map<String, String> params = parseQuery(payload);

        String transactionId = params.get("order_invoice_number");
        String responseCode = params.get("response_code");
        String amountStr = params.get("order_amount");

        String status = "00".equals(responseCode) ? "SUCCESS" : "FAILED";
        BigDecimal amount = amountStr != null ? new BigDecimal(amountStr) : BigDecimal.ZERO;

        return new SePayWebhookData(transactionId, status, amount, params);
    }

    /**
     * Create checkout URL using SePay API
     */
    private String createCheckoutUrl(BigDecimal amountVnd, String bookingCode)
            throws Exception {

        // 1. Build all form fields
        String orderInvoiceNumber = "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);

        String description = "Payment for booking: " + bookingCode;
        String successUrl = sePayConfig.getSuccessUrl() != null ? sePayConfig.getSuccessUrl() :
                "https://apigateway.microservices.appf4s.io.vn/services/msbooking/api/payment/sepay/callback";
        String errorUrl = sePayConfig.getErrorUrl() != null ? sePayConfig.getErrorUrl() : successUrl;
        String cancelUrl = sePayConfig.getCancelUrl() != null ? sePayConfig.getCancelUrl() : successUrl;

        Map<String, String> formData = new LinkedHashMap<>();
        formData.put("merchant", sePayConfig.getMerchantId());
        formData.put("operation", "PURCHASE");
        formData.put("payment_method", "BANK_TRANSFER"); // Add missing payment_method field
        formData.put("order_amount", amountVnd.toPlainString());
        formData.put("currency", "VND");
        formData.put("order_invoice_number", orderInvoiceNumber);
        formData.put("order_description", description);
        formData.put("customer_id", "CUST_001");
        formData.put("success_url", successUrl);
        formData.put("error_url", errorUrl);
        formData.put("cancel_url", cancelUrl);

        // 2. Signature
        String signature = generateSignature(formData);
        formData.put("signature", signature);

        // 3. Encode form body
        String body = buildFormUrlEncoded(formData);

        // 4. HTTP client — FOLLOW redirects to capture checkout URL with configurable timeout
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(java.time.Duration.ofSeconds(sePayConfig.getConnectTimeoutSeconds()))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sePayConfig.getInitUrl()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(java.time.Duration.ofSeconds(sePayConfig.getRequestTimeoutSeconds()))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // 5. Send request with retry logic
        HttpResponse<String> response = null;
        int maxRetries = sePayConfig.getMaxRetries();
        int retryDelay = sePayConfig.getRetryDelayMillis();
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                LOG.debug("Attempting SePay API call {}/{} for transaction: {}", attempt, maxRetries, orderInvoiceNumber);
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                break; // Success, exit retry loop
            } catch (java.net.http.HttpTimeoutException e) {
                LOG.warn("SePay API timeout attempt {}/{} for transaction: {}", attempt, maxRetries, orderInvoiceNumber, e);
                if (attempt == maxRetries) {
                    LOG.error("SePay API timeout after {} attempts for transaction: {}", maxRetries, orderInvoiceNumber, e);
                    throw new RuntimeException("SePay service timeout - please try again", e);
                }
            } catch (java.net.ConnectException e) {
                LOG.warn("Failed to connect to SePay API attempt {}/{} for transaction: {}", attempt, maxRetries, orderInvoiceNumber, e);
                if (attempt == maxRetries) {
                    LOG.error("Failed to connect to SePay API after {} attempts for transaction: {}", maxRetries, orderInvoiceNumber, e);
                    throw new RuntimeException("Unable to connect to SePay service", e);
                }
            } catch (InterruptedException e) {
                LOG.error("SePay API request interrupted for transaction: {}", orderInvoiceNumber, e);
                Thread.currentThread().interrupt(); // Restore interrupt status
                throw new RuntimeException("Payment request was interrupted", e);
            } catch (Exception e) {
                LOG.error("Unexpected error calling SePay API attempt {}/{} for transaction: {}", attempt, maxRetries, orderInvoiceNumber, e);
                if (attempt == maxRetries) {
                    throw new RuntimeException("Failed to create SePay payment", e);
                }
            }
            
            // Wait before retry (except on last attempt)
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Payment request was interrupted during retry delay", ie);
                }
            }
        }

        int status = response.statusCode();
        URI finalUri = response.uri();

        LOG.debug("HTTP Status = " + status);
        LOG.debug("Final URI   = " + finalUri);

        // 6. Return the working checkout URL directly
        if (finalUri != null && !finalUri.toString().isEmpty()) {
            LOG.info("SePay checkout URL created: {}", finalUri.toString());
            return finalUri.toString();
        }

        // 7. Fallback - create mock URL for testing when SePay is unavailable
        String fallbackUrl = String.format(
            "https://sepay-sandbox.com/checkout?merchant=%s&order=%s&amount=%s&signature=%s",
            URLEncoder.encode(sePayConfig.getMerchantId(), StandardCharsets.UTF_8),
            URLEncoder.encode(orderInvoiceNumber, StandardCharsets.UTF_8),
            URLEncoder.encode(amountVnd.toPlainString(), StandardCharsets.UTF_8),
            URLEncoder.encode(signature, StandardCharsets.UTF_8)
        );
        
        LOG.warn("SePay service unavailable, using fallback URL: {}", fallbackUrl);
        LOG.debug("HTTP Status: {}, Response Body: {}", status, response.body());
        return fallbackUrl;
    }

    /**
     * Generate signature according to SePay documentation.
     * signed string: field=value,field2=value2,...
     */
    private String generateSignature(Map<String, String> formData) {
        // Fields to sign according to SePay documentation
        List<String> signedFields = Arrays.asList(
                "merchant",
                "operation",
                "payment_method",
                "order_amount",
                "currency",
                "order_invoice_number",
                "order_description",
                "customer_id",
                "success_url",
                "error_url",
                "cancel_url");

        StringBuilder signedString = new StringBuilder();

        for (String field : signedFields) {
            if (!formData.containsKey(field)) {
                continue; // chỉ ký field thực sự có trong form
            }
            String value = formData.get(field);
            if (value == null || value.isEmpty()) {
                continue; // bỏ qua field có giá trị rỗng
            }
            if (signedString.length() > 0) {
                signedString.append(",");
            }
            signedString.append(field).append("=").append(value);
        }

        String toSign = signedString.toString();
        LOG.debug("Signed string: " + toSign);

        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(
                    sePayConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    /**
     * Validate signature from callback
     */
    private boolean validateSignature(Map<String, String> params) {
        String receivedSignature = params.get("signature");
        if (receivedSignature == null || receivedSignature.isEmpty()) {
            return false;
        }

        // Remove signature from params for validation
        Map<String, String> paramsForValidation = new HashMap<>(params);
        paramsForValidation.remove("signature");

        // Generate expected signature
        String expectedSignature = generateSignature(paramsForValidation);
        
        return receivedSignature.equals(expectedSignature);
    }

    /**
     * Build application/x-www-form-urlencoded body từ formData.
     * LƯU Ý: ký signature bằng giá trị raw, còn body phải URL-encode.
     */
    private String buildFormUrlEncoded(Map<String, String> formData) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                sb.append("=");
                sb.append(URLEncoder.encode(
                        entry.getValue() == null ? "" : entry.getValue(),
                        StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * Parse query string to map
     */
    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (Exception e) {
                    LOG.warn("Failed to decode query parameter: {}", pair, e);
                }
            }
        }
        return params;
    }

    /**
     * SePay callback result
     */
    public static class SePayCallbackResult {
        private final boolean valid;
        private final String transactionId;
        private final String status;
        private final String message;

        public SePayCallbackResult(boolean valid, String transactionId, String status, String message) {
            this.valid = valid;
            this.transactionId = transactionId;
            this.status = status;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * SePay webhook data
     */
    public static class SePayWebhookData {
        private final String transactionId;
        private final String status;
        private final BigDecimal amount;
        private final Map<String, String> rawParams;

        public SePayWebhookData(String transactionId, String status, BigDecimal amount, Map<String, String> rawParams) {
            this.transactionId = transactionId;
            this.status = status;
            this.amount = amount;
            this.rawParams = rawParams;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getStatus() {
            return status;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public Map<String, String> getRawParams() {
            return rawParams;
        }
    }
}