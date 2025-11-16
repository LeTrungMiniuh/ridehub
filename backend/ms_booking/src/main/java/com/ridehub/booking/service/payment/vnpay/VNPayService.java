package com.ridehub.booking.service.payment.vnpay;

import com.ridehub.booking.domain.PaymentTransaction;
import com.ridehub.booking.service.vm.InitiatePaymentRequestVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * VNPay payment service implementation
 */
@Service
public class VNPayService {

    private static final Logger LOG = LoggerFactory.getLogger(VNPayService.class);

    private final VNPayConfig vnPayConfig;
    private final RestTemplate restTemplate;

    public VNPayService(VNPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Create VNPay payment URL
     * @param bookingExpiresAt 
     */
    public String createPaymentUrl(InitiatePaymentRequestVM request, String transactionId,
            String orderRef, BigDecimal amount, String returnUrl, String ipAddress, Instant bookingExpiresAt) {
        LOG.debug("Creating VNPay payment URL for transaction: {}", transactionId);

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnPayConfig.getVersion());
        vnpParams.put("vnp_Command", vnPayConfig.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount.multiply(new BigDecimal("10000")).longValue()));
        vnpParams.put("vnp_CurrCode", vnPayConfig.getCurrCode());
        vnpParams.put("vnp_TxnRef", transactionId);
        vnpParams.put("vnp_OrderInfo", "Payment for booking: " + orderRef);
        vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnpParams.put("vnp_Locale", vnPayConfig.getLocale());
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", ipAddress);
        vnpParams.put("vnp_CreateDate", VNPayUtils.getVNPayDate());
        vnpParams.put("vnp_ExpireDate", VNPayUtils.getVNPayExpireDate(bookingExpiresAt));

        // Build query string
        String query = VNPayUtils.buildQuery(vnpParams);

        // Generate secure hash
        String secureHash = VNPayUtils.hmacSHA512(vnPayConfig.getHashSecret(), query);

        // Build final payment URL
        String paymentUrl = vnPayConfig.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;

        LOG.debug("VNPay payment URL created successfully for transaction: {}", transactionId);
        return paymentUrl;
    }

    /**
     * Verify VNPay callback/webhook
     */
    public VNPayCallbackResult verifyCallback(Map<String, String> params) {
        LOG.debug("Verifying VNPay callback");

        // Validate signature
        if (!VNPayUtils.validateSignature(new HashMap<>(params), vnPayConfig.getHashSecret())) {
            LOG.warn("Invalid VNPay callback signature");
            return new VNPayCallbackResult(false, null, null, "Invalid signature");
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionId = params.get("vnp_TxnRef");
        String amount = params.get("vnp_Amount");

        boolean isSuccess = "00".equals(responseCode);
        String status = isSuccess ? "SUCCESS" : "FAILED";

        LOG.debug("VNPay callback verified - Status: {}, Transaction: {}", status, transactionId);

        return new VNPayCallbackResult(true, transactionId, status,
                isSuccess ? "Payment successful" : "Payment failed: " + responseCode);
    }

    /**
     * Parse VNPay webhook payload
     * Based on VNPay IPN URL documentation: https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html#code-ipn-url
     */
    public VNPayWebhookData parseWebhookPayload(String payload) {
        LOG.debug("Parsing VNPay webhook payload");

        Map<String, String> params = VNPayUtils.parseQuery(payload);

        // Required parameters according to VNPay documentation
        String transactionId = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String amountStr = params.get("vnp_Amount");
        String tmnCode = params.get("vnp_TmnCode");
        String bankCode = params.get("vnp_BankCode");
        String bankTranNo = params.get("vnp_BankTranNo");
        String cardType = params.get("vnp_CardType");
        String orderInfo = params.get("vnp_OrderInfo");
        String payDate = params.get("vnp_PayDate");
        String transactionNo = params.get("vnp_TransactionNo");
        String secureHash = params.get("vnp_SecureHash");

        // Validate required parameters
        if (transactionId == null || responseCode == null || transactionStatus == null || amountStr == null) {
            LOG.warn("Missing required VNPay IPN parameters");
            throw new IllegalArgumentException("Missing required VNPay IPN parameters");
        }

        // Convert amount from VNPAY format (multiply by 100) to actual amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr).divide(new BigDecimal("100"));
        } catch (NumberFormatException e) {
            LOG.error("Invalid amount format: {}", amountStr);
            throw new IllegalArgumentException("Invalid amount format: " + amountStr);
        }

        // Determine payment status based on both response code and transaction status
        // According to VNPay documentation: 
        // - vnp_ResponseCode: Response code from VNPAY (00 = success)
        // - vnp_TransactionStatus: Transaction status at VNPAY (00 = success)
        String status;
        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            status = "SUCCESS";
        } else if ("01".equals(transactionStatus)) {
            status = "PROCESSING";
        } else if ("02".equals(transactionStatus)) {
            status = "FAILED";
        } else {
            status = "FAILED";
        }

        // Log important details for debugging
        LOG.info("VNPay IPN parsed - TransactionId: {}, ResponseCode: {}, TransactionStatus: {}, Amount: {}, BankCode: {}",
                transactionId, responseCode, transactionStatus, amount, bankCode);

        return new VNPayWebhookData(transactionId, status, amount, params);
    }

    /**
     * Query transaction status from VNPay using PaymentTransaction data
     */
    public VNPayQueryResult queryTransaction(PaymentTransaction tx, String ip) {
        String txnRef = tx.getTransactionId();
        String orderRef = tx.getOrderRef();

        // Use the ORIGINAL merchant-side create date (GMT+7, yyyyMMddHHmmss)
        // This should be the same value you sent as vnp_CreateDate in the pay request.
        String transactionDate = tx.getGatewayCreateDate(); // must already be GMT+7 format
        if (transactionDate == null || transactionDate.length() != 14) {
            // Better to fail fast than send wrong date: VNPay will not match the
            // transaction
            return new VNPayQueryResult(false, "03", "Missing/invalid vnp_TransactionDate", null, null);
        }

        // Each query request has its own createDate (GMT+7) for the query itself
        // Generate a more unique request ID using timestamp + random UUID to avoid duplicates
        String requestId = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().replace("-", "");
        String createDate = VNPayUtils.getVNPayDate(); // GMT+7, yyyyMMddHHmmss

        Map<String, String> params = new HashMap<>();
        params.put("vnp_RequestId", requestId);
        params.put("vnp_Version", vnPayConfig.getVersion());
        params.put("vnp_Command", "querydr");
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_TransactionDate", transactionDate);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_IpAddr", ip);
        params.put("vnp_OrderInfo",
                orderRef != null ? "Query transaction for order: " + orderRef : "Query transaction: " + txnRef);

        // EXACT spec order for checksum:
        String data = String.join("|",
                requestId,
                vnPayConfig.getVersion(),
                "querydr",
                vnPayConfig.getTmnCode(),
                txnRef,
                transactionDate,
                createDate,
                ip,
                params.get("vnp_OrderInfo"));

        String secureHash = VNPayUtils.hmacSHA512(vnPayConfig.getHashSecret(), data);
        params.put("vnp_SecureHash", secureHash);

        Map<String, Object> res = restTemplate.postForObject(vnPayConfig.getQueryUrl(), params, Map.class);
        return res != null ? parseQueryResponse(res)
                : new VNPayQueryResult(false, "99", "No response from VNPay", null, null);
    }

    /**
     * Query transaction status from VNPay with explicit parameters (legacy method
     * for backward compatibility)
     */
    public VNPayQueryResult queryTransaction(String transactionId, String ipAddress, String transactionDate,
            String orderRef) {
        LOG.debug("Querying VNPay transaction status: {} for orderRef: {}", transactionId, orderRef);

        try {
            // Generate unique request ID
            String requestId = UUID.randomUUID().toString().replace("-", "");
            String createDate = VNPayUtils.getVNPayDate();

            // Build query parameters
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("vnp_RequestId", requestId);
            queryParams.put("vnp_Version", vnPayConfig.getVersion());
            queryParams.put("vnp_Command", "querydr");
            queryParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            queryParams.put("vnp_TxnRef", transactionId);
            queryParams.put("vnp_OrderInfo", orderRef != null ? "Query transaction for order: " + orderRef
                    : "Query transaction: " + transactionId);
            queryParams.put("vnp_TransactionDate", transactionDate != null ? transactionDate : createDate);
            queryParams.put("vnp_CreateDate", createDate);
            queryParams.put("vnp_IpAddr", ipAddress);

            // Build data for hash
            String hashData = String.join("|",
                    requestId,
                    vnPayConfig.getVersion(),
                    "querydr",
                    vnPayConfig.getTmnCode(),
                    transactionId,
                    orderRef != null ? "Query transaction for order: " + orderRef
                            : "Query transaction: " + transactionId,
                    transactionDate != null ? transactionDate : createDate,
                    createDate,
                    ipAddress);

            // Generate secure hash
            String secureHash = VNPayUtils.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            queryParams.put("vnp_SecureHash", secureHash);

            // Make API call
            Map<String, Object> response = restTemplate.postForObject(
                    vnPayConfig.getQueryUrl(),
                    queryParams,
                    Map.class);

            if (response != null) {
                return parseQueryResponse(response);
            } else {
                return new VNPayQueryResult(false, "99", "No response from VNPay", null, null);
            }

        } catch (Exception e) {
            LOG.error("Error querying VNPay transaction: {} for orderRef: {}", transactionId, orderRef, e);
            return new VNPayQueryResult(false, "99", "Error: " + e.getMessage(), null, null);
        }
    }

    /**
     * Refund transaction
     */
    public VNPayRefundResult refundTransaction(String transactionId, BigDecimal amount,
            String ipAddress, String orderInfo, String transactionType, String originalTransDate) {
        LOG.debug("Refunding VNPay transaction: {} amount: {}", transactionId, amount);

        try {
            // Generate unique request ID
            String requestId = UUID.randomUUID().toString().replace("-", "");
            String createDate = VNPayUtils.getVNPayDate();
            String amountInVND = String.valueOf(amount.multiply(new BigDecimal("100")).longValue());

            // Build refund parameters
            Map<String, String> refundParams = new HashMap<>();
            refundParams.put("vnp_RequestId", requestId);
            refundParams.put("vnp_Version", vnPayConfig.getVersion());
            refundParams.put("vnp_Command", "refund");
            refundParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            refundParams.put("vnp_TransactionType",
                    transactionType != null ? transactionType : vnPayConfig.getRefundTransactionTypeFull()); // 02: full
                                                                                                             // refund,
                                                                                                             // 03:
                                                                                                             // partial
                                                                                                             // refund
            refundParams.put("vnp_TxnRef", transactionId);
            refundParams.put("vnp_Amount", amountInVND);
            refundParams.put("vnp_OrderInfo",
                    orderInfo != null ? orderInfo : "Refund for transaction: " + transactionId);
            refundParams.put("vnp_CreateBy", vnPayConfig.getRefundCreateBy());
            refundParams.put("vnp_CreateDate", createDate);
            refundParams.put("vnp_IpAddr", ipAddress);
            refundParams.put("vnp_TransactionDate", originalTransDate); // Required parameter per VNPay spec

            // Build data for hash according to VNPay specification:
            // data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" + 
            //        vnp_TransactionType + "|" + vnp_TxnRef + "|" + vnp_Amount + "|" + vnp_TransactionNo + "|" + 
            //        vnp_TransactionDate + "|" + vnp_CreateBy + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo
            String transactionTypeParam = transactionType != null ? transactionType : vnPayConfig.getRefundTransactionTypeFull();
            String orderInfoParam = orderInfo != null ? orderInfo : "Refund for transaction: " + transactionId;
            
            String hashData = String.join("|",
                    requestId,
                    vnPayConfig.getVersion(),
                    "refund",
                    vnPayConfig.getTmnCode(),
                    transactionTypeParam,
                    transactionId,
                    amountInVND,
                    "", // vnp_TransactionNo - optional, empty for now
                    originalTransDate, // vnp_TransactionDate - required
                    vnPayConfig.getRefundCreateBy(),
                    createDate,
                    ipAddress,
                    orderInfoParam);

            // Generate secure hash
            String secureHash = VNPayUtils.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            refundParams.put("vnp_SecureHash", secureHash);

            // Make API call
            Map<String, Object> response = restTemplate.postForObject(
                    vnPayConfig.getQueryUrl(),
                    refundParams,
                    Map.class);

            if (response != null) {
                return parseRefundResponse(response);
            } else {
                return new VNPayRefundResult(false, "99", "No response from VNPay", null, null, null);
            }

        } catch (Exception e) {
            LOG.error("Error refunding VNPay transaction: {}", transactionId, e);
            return new VNPayRefundResult(false, "99", "Error: " + e.getMessage(), null, null, null);
        }
    }

    /**
     * Parse query response from VNPay
     */
    private VNPayQueryResult parseQueryResponse(Map<String, Object> response) {
        String responseCode = String.valueOf(response.get("vnp_ResponseCode"));
        String message = String.valueOf(response.get("vnp_Message"));
        String transactionStatus = response.containsKey("vnp_TransactionStatus")
                ? String.valueOf(response.get("vnp_TransactionStatus"))
                : null;

        // Extract amount from response if available
        BigDecimal amount = null;
        if (response.containsKey("vnp_Amount")) {
            String amountStr = String.valueOf(response.get("vnp_Amount"));
            if (amountStr != null && !amountStr.isEmpty()) {
                try {
                    amount = new BigDecimal(amountStr).divide(new BigDecimal("100"));
                } catch (NumberFormatException e) {
                    LOG.warn("Could not parse amount from VNPay response: {}", amountStr);
                }
            }
        }

        boolean isSuccess = "00".equals(responseCode);
        
        // Handle special case for code=94 (duplicate request)
        // This is not actually an error - it means the request was processed recently
        if ("94".equals(responseCode)) {
            LOG.debug("VNPay duplicate request detected (code=94): {} - treating as success", message);
            // For duplicate requests, we consider it successful since the transaction was processed
            return new VNPayQueryResult(true, responseCode, "Duplicate request - transaction processed", 
                transactionStatus, amount);
        }
        
        return new VNPayQueryResult(isSuccess, responseCode, message, transactionStatus, amount);
    }

    /**
     * Parse refund response from VNPay
     */
    private VNPayRefundResult parseRefundResponse(Map<String, Object> response) {
        String responseCode = String.valueOf(response.get("vnp_ResponseCode"));
        String message = String.valueOf(response.get("vnp_Message"));
        String transactionNo = response.containsKey("vnp_TransactionNo")
                ? String.valueOf(response.get("vnp_TransactionNo"))
                : null;
        String transactionType = response.containsKey("vnp_TransactionType")
                ? String.valueOf(response.get("vnp_TransactionType"))
                : null;
        String transactionStatus = response.containsKey("vnp_TransactionStatus")
                ? String.valueOf(response.get("vnp_TransactionStatus"))
                : null;

        boolean isSuccess = "00".equals(responseCode);
        return new VNPayRefundResult(isSuccess, responseCode, message, transactionNo,
                transactionType, transactionStatus);
    }

    /**
     * VNPay callback result
     */
    public static class VNPayCallbackResult {
        private final boolean valid;
        private final String transactionId;
        private final String status;
        private final String message;

        public VNPayCallbackResult(boolean valid, String transactionId, String status, String message) {
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
     * VNPay webhook data
     */
    public static class VNPayWebhookData {
        private final String transactionId;
        private final String status;
        private final BigDecimal amount;
        private final Map<String, String> rawParams;

        public VNPayWebhookData(String transactionId, String status, BigDecimal amount, Map<String, String> rawParams) {
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
     * VNPay query result
     */
    public static class VNPayQueryResult {
        private final boolean success;
        private final String responseCode;
        private final String message;
        private final String transactionStatus;
        private final BigDecimal amount;

        public VNPayQueryResult(boolean success, String responseCode, String message, String transactionStatus) {
            this(success, responseCode, message, transactionStatus, null);
        }

        public VNPayQueryResult(boolean success, String responseCode, String message, String transactionStatus,
                BigDecimal amount) {
            this.success = success;
            this.responseCode = responseCode;
            this.message = message;
            this.transactionStatus = transactionStatus;
            this.amount = amount;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public String getMessage() {
            return message;
        }

        public String getTransactionStatus() {
            return transactionStatus;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    /**
     * VNPay refund result
     */
    public static class VNPayRefundResult {
        private final boolean success;
        private final String responseCode;
        private final String message;
        private final String transactionNo;
        private final String transactionType;
        private final String transactionStatus;

        public VNPayRefundResult(boolean success, String responseCode, String message,
                String transactionNo, String transactionType, String transactionStatus) {
            this.success = success;
            this.responseCode = responseCode;
            this.message = message;
            this.transactionNo = transactionNo;
            this.transactionType = transactionType;
            this.transactionStatus = transactionStatus;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public String getMessage() {
            return message;
        }

        public String getTransactionNo() {
            return transactionNo;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public String getTransactionStatus() {
            return transactionStatus;
        }
    }
}
