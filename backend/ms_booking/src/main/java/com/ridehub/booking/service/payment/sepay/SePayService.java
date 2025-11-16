package com.ridehub.booking.service.payment.sepay;

import com.ridehub.booking.domain.PaymentTransaction;
import com.ridehub.booking.service.vm.InitiatePaymentRequestVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            String checkoutUrl = createCheckoutUrl(amount, transactionId);
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

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> handleSuccess(@RequestParam Map<String, String> params) {
        LOG.info("SePay SUCCESS redirect received: {}", params);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("transactionId", params.get("order_invoice_number"));
        result.put("message", "Payment success redirect");

        return ResponseEntity.ok(result);
    }

    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(@RequestParam Map<String, String> params) {
        LOG.info("SePay ERROR redirect received: {}", params);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("transactionId", params.get("order_invoice_number"));
        result.put("message", params.getOrDefault("message", "Payment error"));

        return ResponseEntity.ok(result);
    }

    /**
     * Parse SePay webhook payload
     */
    public SePayWebhookData parseWebhookPayload(String payload) {
        LOG.debug("Parsing SePay webhook payload");

        try {
            // Try to parse as JSON first (new IPN format)
            if (payload.trim().startsWith("{")) {
                return parseJsonWebhookPayload(payload);
            } else {
                // Fallback to query string format (legacy)
                Map<String, String> params = parseQuery(payload);
                String transactionId = params.get("order_invoice_number");
                String responseCode = params.get("order_status");
                String amountStr = params.get("order_amount");

                String status = "CAPTURED".equals(responseCode) ? "SUCCESS" : "FAILED";
                BigDecimal amount = amountStr != null ? new BigDecimal(amountStr) : BigDecimal.ZERO;

                return new SePayWebhookData(transactionId, status, amount, params);
            }
        } catch (Exception e) {
            LOG.error("Error parsing SePay webhook payload: {}", payload, e);
            throw new RuntimeException("Failed to parse webhook payload", e);
        }
    }

    /**
     * Parse JSON webhook payload (new IPN format)
     */
    private SePayWebhookData parseJsonWebhookPayload(String payload) {
        LOG.debug("Parsing JSON webhook payload");

        String notificationType = extractJsonField(payload, "notification_type");
        if (!"ORDER_PAID".equals(notificationType)) {
            LOG.warn("Unexpected notification type: {}", notificationType);
        }

        // Extract order information
        String orderSection = extractJsonObject(payload, "order");
        if (orderSection == null) {
            throw new RuntimeException("Invalid webhook format: missing 'order' section");
        }

        String orderInvoiceNumber = extractJsonField(orderSection, "order_invoice_number");
        String orderStatus = extractJsonField(orderSection, "order_status");
        String amountStr = extractJsonField(orderSection, "order_amount");
        String orderId = extractJsonField(orderSection, "order_id");
        String orderCurrency = extractJsonField(orderSection, "order_currency");
        String orderDescription = extractJsonField(orderSection, "order_description");

        // Extract transaction information
        String transactionSection = extractJsonObject(payload, "transaction");
        String transactionId = extractJsonField(transactionSection, "transaction_id");
        String transactionStatus = extractJsonField(transactionSection, "transaction_status");
        String paymentMethod = extractJsonField(transactionSection, "payment_method");
        String cardNumber = extractJsonField(transactionSection, "card_number");
        String cardHolderName = extractJsonField(transactionSection, "card_holder_name");

        // Extract customer information
        String customerSection = extractJsonObject(payload, "customer");
        String customerId = extractJsonField(customerSection, "customer_id");

        // Determine payment status
        String status = "CAPTURED".equals(orderStatus) && "APPROVED".equals(transactionStatus)
                ? "SUCCESS"
                : "FAILED";
        BigDecimal amount = amountStr != null ? new BigDecimal(amountStr) : BigDecimal.ZERO;

        // Create raw params map for compatibility
        Map<String, String> rawParams = new HashMap<>();
        rawParams.put("notification_type", notificationType);
        rawParams.put("order_id", orderId);
        rawParams.put("order_invoice_number", orderInvoiceNumber);
        rawParams.put("order_status", orderStatus);
        rawParams.put("order_amount", amountStr);
        rawParams.put("order_currency", orderCurrency);
        rawParams.put("order_description", orderDescription);
        rawParams.put("transaction_id", transactionId);
        rawParams.put("transaction_status", transactionStatus);
        rawParams.put("payment_method", paymentMethod);
        rawParams.put("card_number", cardNumber);
        rawParams.put("card_holder_name", cardHolderName);
        rawParams.put("customer_id", customerId);

        LOG.debug("Parsed JSON webhook - Transaction: {}, Status: {}, Amount: {}",
                orderInvoiceNumber, status, amount);

        return new SePayWebhookData(orderInvoiceNumber, status, amount, rawParams);
    }

    /**
     * Query order detail from SePay API
     */
    public SePayOrderDetail queryOrderDetail(String transactionId) {
        LOG.info("Querying SePay order detail for order ID: {}", transactionId);

        try {
            String apiUrl = sePayConfig.getApiBaseUrl() + "/v1/order/detail/" + transactionId;

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(sePayConfig.getConnectTimeoutSeconds()))
                    .build();
            String auth = Base64.getEncoder()
                    .encodeToString((sePayConfig.getMerchantId() + ":" + sePayConfig.getSecretKey()).getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            apiUrl))
                    .header("Authorization", "Basic " + auth)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            LOG.info("SePay API response status: {}, body: {}", statusCode, responseBody);

            if (statusCode == 200) {
                return parseOrderDetailResponse(responseBody);
            } else {
                LOG.error("SePay API error - Status: {}, Body: {}", statusCode, responseBody);
                throw new RuntimeException("Failed to query order detail: HTTP " + statusCode);
            }

        } catch (java.net.http.HttpTimeoutException e) {
            LOG.error("SePay API timeout while querying order detail: {}", transactionId, e);
            throw new RuntimeException("SePay service timeout - please try again", e);
        } catch (java.net.ConnectException e) {
            LOG.error("Failed to connect to SePay API while querying order detail: {}", transactionId, e);
            throw new RuntimeException("Unable to connect to SePay service", e);
        } catch (InterruptedException e) {
            LOG.error("SePay API request interrupted while querying order detail: {}", transactionId, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Order detail query was interrupted", e);
        } catch (Exception e) {
            LOG.error("Error querying SePay order detail: {}", transactionId, e);
            throw new RuntimeException("Failed to query order detail", e);
        }
    }

    /**
     * Create checkout URL using SePay API
     */
    private String createCheckoutUrl(BigDecimal amountVnd, String transactionId)
            throws Exception {

        String description = "Payment for booking: " + transactionId;
        String successUrl = sePayConfig.getSuccessUrl() != null ? sePayConfig.getSuccessUrl()
                : "https://apigateway.microservices.appf4s.io.vn/services/msbooking/api/payment/sepay/callback";
        String errorUrl = sePayConfig.getErrorUrl() != null ? sePayConfig.getErrorUrl() : successUrl;
        String cancelUrl = sePayConfig.getCancelUrl() != null ? sePayConfig.getCancelUrl() : successUrl;

        Map<String, String> formData = new LinkedHashMap<>();
        formData.put("merchant", sePayConfig.getMerchantId());
        formData.put("operation", "PURCHASE");
        formData.put("payment_method", "BANK_TRANSFER"); // Add missing payment_method field
        formData.put("order_amount", amountVnd.toPlainString());
        formData.put("currency", "VND");
        formData.put("order_invoice_number", transactionId);
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

        // 4. HTTP client — FOLLOW redirects to capture checkout URL with configurable
        // timeout
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
                LOG.debug("Attempting SePay API call {}/{} for transaction: {}", attempt, maxRetries,
                        transactionId);
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                break; // Success, exit retry loop
            } catch (java.net.http.HttpTimeoutException e) {
                LOG.warn("SePay API timeout attempt {}/{} for transaction: {}", attempt, maxRetries,
                        transactionId,
                        e);
                if (attempt == maxRetries) {
                    LOG.error("SePay API timeout after {} attempts for transaction: {}", maxRetries,
                            transactionId,
                            e);
                    throw new RuntimeException("SePay service timeout - please try again", e);
                }
            } catch (java.net.ConnectException e) {
                LOG.warn("Failed to connect to SePay API attempt {}/{} for transaction: {}", attempt, maxRetries,
                        transactionId, e);
                if (attempt == maxRetries) {
                    LOG.error("Failed to connect to SePay API after {} attempts for transaction: {}", maxRetries,
                            transactionId, e);
                    throw new RuntimeException("Unable to connect to SePay service", e);
                }
            } catch (InterruptedException e) {
                LOG.error("SePay API request interrupted for transaction: {}", transactionId, e);
                Thread.currentThread().interrupt(); // Restore interrupt status
                throw new RuntimeException("Payment request was interrupted", e);
            } catch (Exception e) {
                LOG.error("Unexpected error calling SePay API attempt {}/{} for transaction: {}", attempt, maxRetries,
                        transactionId, e);
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
                URLEncoder.encode(
                        transactionId, StandardCharsets.UTF_8),
                URLEncoder.encode(amountVnd.toPlainString(), StandardCharsets.UTF_8),
                URLEncoder.encode(signature, StandardCharsets.UTF_8));

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
     * Parse order detail response from SePay API
     */
    private SePayOrderDetail parseOrderDetailResponse(String responseBody) {
        try {
            // Simple JSON parsing using string manipulation (avoiding external
            // dependencies)
            // In production, consider using Jackson or Gson

            String dataSection = extractJsonObject(responseBody, "data");
            if (dataSection == null) {
                throw new RuntimeException("Invalid response format: missing 'data' field");
            }

            String id = extractJsonField(dataSection, "id");
            String customerId = extractJsonField(dataSection, "customer_id");
            String orderId = extractJsonField(dataSection, "order_id");
            String orderInvoiceNumber = extractJsonField(dataSection, "order_invoice_number");
            String orderStatus = extractJsonField(dataSection, "order_status");
            String orderAmount = extractJsonField(dataSection, "order_amount");
            String orderCurrency = extractJsonField(dataSection, "order_currency");
            String orderDescription = extractJsonField(dataSection, "order_description");
            String authenticationStatus = extractJsonField(dataSection, "authentication_status");
            String createdAt = extractJsonField(dataSection, "created_at");
            String updatedAt = extractJsonField(dataSection, "updated_at");

            List<SePayTransaction> transactions = parseTransactions(dataSection);

            return new SePayOrderDetail(
                    id, customerId, orderId, orderInvoiceNumber, orderStatus,
                    orderAmount, orderCurrency, orderDescription, authenticationStatus,
                    createdAt, updatedAt, transactions);

        } catch (Exception e) {
            LOG.error("Error parsing SePay order detail response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse order detail response", e);
        }
    }

    /**
     * Extract JSON field value from JSON string
     */
    private String extractJsonField(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }

        // Try for null values
        pattern = "\"" + fieldName + "\"\\s*:\\s*(null)";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(json);
        if (m.find()) {
            return null;
        }

        return null;
    }

    /**
     * Extract JSON object from JSON string
     */
    private String extractJsonObject(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\\{([^}]*)\\}";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return "{" + m.group(1) + "}";
        }

        // Try for nested objects (more complex)
        pattern = "\"" + fieldName + "\"\\s*:\\s*(\\{.*?\\})(?=,|})";
        p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    /**
     * Parse transactions array from response
     */
    private List<SePayTransaction> parseTransactions(String dataSection) {
        List<SePayTransaction> transactions = new ArrayList<>();

        // Extract transactions array
        String transactionsPattern = "\"transactions\"\\s*:\\s*\\[(.*?)\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(transactionsPattern,
                java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(dataSection);

        if (!m.find()) {
            return transactions;
        }

        String transactionsArray = m.group(1);

        // Split by }{ (between objects) and parse each transaction
        String[] transactionObjects = transactionsArray.split("(?<=\\}),\\s*(?=\\{)");

        for (String transactionObj : transactionObjects) {
            try {
                // Clean up the object string
                transactionObj = transactionObj.trim();
                if (!transactionObj.startsWith("{")) {
                    transactionObj = "{" + transactionObj;
                }
                if (!transactionObj.endsWith("}")) {
                    transactionObj = transactionObj + "}";
                }

                String id = extractJsonField(transactionObj, "id");
                String paymentMethod = extractJsonField(transactionObj, "payment_method");
                String transactionType = extractJsonField(transactionObj, "transaction_type");
                String transactionAmount = extractJsonField(transactionObj, "transaction_amount");
                String transactionCurrency = extractJsonField(transactionObj, "transaction_currency");
                String transactionStatus = extractJsonField(transactionObj, "transaction_status");
                String authStatus = extractJsonField(transactionObj, "authentication_status");
                String cardNumber = extractJsonField(transactionObj, "card_number");
                String cardHolderName = extractJsonField(transactionObj, "card_holder_name");
                String cardExpiry = extractJsonField(transactionObj, "card_expiry");
                String cardFundingMethod = extractJsonField(transactionObj, "card_funding_method");
                String cardBrand = extractJsonField(transactionObj, "card_brand");
                String transactionDate = extractJsonField(transactionObj, "transaction_date");
                String transactionLastUpdatedDate = extractJsonField(transactionObj, "transaction_last_updated_date");

                SePayTransaction transaction = new SePayTransaction(
                        id, paymentMethod, transactionType, transactionAmount, transactionCurrency,
                        transactionStatus, authStatus, cardNumber, cardHolderName, cardExpiry,
                        cardFundingMethod, cardBrand, transactionDate, transactionLastUpdatedDate);

                transactions.add(transaction);

            } catch (Exception e) {
                LOG.warn("Failed to parse transaction object: {}", transactionObj, e);
            }
        }

        return transactions;
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

    /**
     * SePay order detail response
     */
    public static class SePayOrderDetail {
        private final String id;
        private final String customerId;
        private final String orderId;
        private final String orderInvoiceNumber;
        private final String orderStatus;
        private final String orderAmount;
        private final String orderCurrency;
        private final String orderDescription;
        private final String authenticationStatus;
        private final String createdAt;
        private final String updatedAt;
        private final List<SePayTransaction> transactions;

        public SePayOrderDetail(String id, String customerId, String orderId, String orderInvoiceNumber,
                String orderStatus, String orderAmount, String orderCurrency, String orderDescription,
                String authenticationStatus, String createdAt, String updatedAt,
                List<SePayTransaction> transactions) {
            this.id = id;
            this.customerId = customerId;
            this.orderId = orderId;
            this.orderInvoiceNumber = orderInvoiceNumber;
            this.orderStatus = orderStatus;
            this.orderAmount = orderAmount;
            this.orderCurrency = orderCurrency;
            this.orderDescription = orderDescription;
            this.authenticationStatus = authenticationStatus;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.transactions = transactions;
        }

        public String getId() {
            return id;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getOrderInvoiceNumber() {
            return orderInvoiceNumber;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public String getOrderAmount() {
            return orderAmount;
        }

        public String getOrderCurrency() {
            return orderCurrency;
        }

        public String getOrderDescription() {
            return orderDescription;
        }

        public String getAuthenticationStatus() {
            return authenticationStatus;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public List<SePayTransaction> getTransactions() {
            return transactions;
        }

        @Override
        public String toString() {
            return "SePayOrderDetail{" +
                    "id='" + id + '\'' +
                    ", orderId='" + orderId + '\'' +
                    ", orderInvoiceNumber='" + orderInvoiceNumber + '\'' +
                    ", orderStatus='" + orderStatus + '\'' +
                    ", orderAmount='" + orderAmount + '\'' +
                    ", orderCurrency='" + orderCurrency + '\'' +
                    ", orderDescription='" + orderDescription + '\'' +
                    ", authenticationStatus='" + authenticationStatus + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    ", transactions=" + transactions.size() +
                    '}';
        }
    }

    /**
     * SePay transaction detail
     */
    public static class SePayTransaction {
        private final String id;
        private final String paymentMethod;
        private final String transactionType;
        private final String transactionAmount;
        private final String transactionCurrency;
        private final String transactionStatus;
        private final String authenticationStatus;
        private final String cardNumber;
        private final String cardHolderName;
        private final String cardExpiry;
        private final String cardFundingMethod;
        private final String cardBrand;
        private final String transactionDate;
        private final String transactionLastUpdatedDate;

        public SePayTransaction(String id, String paymentMethod, String transactionType,
                String transactionAmount, String transactionCurrency, String transactionStatus,
                String authenticationStatus, String cardNumber, String cardHolderName,
                String cardExpiry, String cardFundingMethod, String cardBrand,
                String transactionDate, String transactionLastUpdatedDate) {
            this.id = id;
            this.paymentMethod = paymentMethod;
            this.transactionType = transactionType;
            this.transactionAmount = transactionAmount;
            this.transactionCurrency = transactionCurrency;
            this.transactionStatus = transactionStatus;
            this.authenticationStatus = authenticationStatus;
            this.cardNumber = cardNumber;
            this.cardHolderName = cardHolderName;
            this.cardExpiry = cardExpiry;
            this.cardFundingMethod = cardFundingMethod;
            this.cardBrand = cardBrand;
            this.transactionDate = transactionDate;
            this.transactionLastUpdatedDate = transactionLastUpdatedDate;
        }

        public String getId() {
            return id;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public String getTransactionAmount() {
            return transactionAmount;
        }

        public String getTransactionCurrency() {
            return transactionCurrency;
        }

        public String getTransactionStatus() {
            return transactionStatus;
        }

        public String getAuthenticationStatus() {
            return authenticationStatus;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public String getCardExpiry() {
            return cardExpiry;
        }

        public String getCardFundingMethod() {
            return cardFundingMethod;
        }

        public String getCardBrand() {
            return cardBrand;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public String getTransactionLastUpdatedDate() {
            return transactionLastUpdatedDate;
        }

        @Override
        public String toString() {
            return "SePayTransaction{" +
                    "id='" + id + '\'' +
                    ", paymentMethod='" + paymentMethod + '\'' +
                    ", transactionType='" + transactionType + '\'' +
                    ", transactionAmount='" + transactionAmount + '\'' +
                    ", transactionStatus='" + transactionStatus + '\'' +
                    ", cardNumber='" + cardNumber + '\'' +
                    ", cardHolderName='" + cardHolderName + '\'' +
                    ", cardBrand='" + cardBrand + '\'' +
                    ", transactionDate='" + transactionDate + '\'' +
                    '}';
        }
    }
}