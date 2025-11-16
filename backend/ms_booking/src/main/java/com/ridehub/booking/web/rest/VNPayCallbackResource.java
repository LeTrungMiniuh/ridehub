package com.ridehub.booking.web.rest;

import com.ridehub.booking.domain.PaymentTransaction;
import com.ridehub.booking.domain.enumeration.PaymentMethod;
import com.ridehub.booking.domain.enumeration.PaymentStatus;
import com.ridehub.booking.repository.PaymentTransactionRepository;
import com.ridehub.booking.service.PaymentService;
import com.ridehub.booking.service.VNPayPollingService;
import com.ridehub.booking.service.dto.RefundRequestDTO;

import com.ridehub.booking.service.payment.vnpay.VNPayConfig;
import com.ridehub.booking.service.payment.vnpay.VNPayService;
import com.ridehub.booking.service.payment.vnpay.VNPayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for handling VNPay callbacks and webhooks.
 */
@RestController
@RequestMapping("/api/payment/vnpay")
public class VNPayCallbackResource {

    private static final Logger LOG = LoggerFactory.getLogger(VNPayCallbackResource.class);

    private final PaymentService paymentService;
    private final VNPayService vnPayService;
    private final VNPayPollingService vnPayPollingService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final VNPayConfig vnpayConfig;

    public VNPayCallbackResource(PaymentService paymentService, VNPayService vnPayService,
            VNPayPollingService vnPayPollingService, PaymentTransactionRepository paymentTransactionRepository,
            VNPayConfig vnpayConfig) {
        this.paymentService = paymentService;
        this.vnPayService = vnPayService;
        this.vnPayPollingService = vnPayPollingService;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.vnpayConfig = vnpayConfig;
    }

    /**
     * Handle VNPay return callback (user redirected back to merchant site)
     */
    @GetMapping("/callback")
    public void handleCallback(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Extract parameters
            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                if (values.length > 0)
                    params.put(key, values[0]);
            });

            // Validate VNPay signature
            VNPayService.VNPayCallbackResult result = vnPayService.verifyCallback(params);
            String base = vnpayConfig.getReturnFEURL();

            // Build final redirect
            String redirectUrl = base
                    + "?status=" + URLEncoder.encode(result.isValid() ? "success" : "error", StandardCharsets.UTF_8)
                    + "&message=" + URLEncoder.encode(result.getMessage(), StandardCharsets.UTF_8)
                    + (result.isValid()
                            ? "&transactionId=" + URLEncoder.encode(result.getTransactionId(), StandardCharsets.UTF_8)
                                    + "&paymentStatus=" + URLEncoder.encode(result.getStatus(), StandardCharsets.UTF_8)
                            : "");

            // Redirect to frontend
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            try {
                response.sendRedirect(vnpayConfig.getReturnFEURL() + "?status=error&message=server_error");
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Handle VNPay IPN (Instant Payment Notification) webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {
        LOG.debug("Received VNPay webhook");

        try {
            String result = paymentService.processWebhook("VNPAY", payload, signature);

            if ("SUCCESS".equals(result)) {
                return ResponseEntity.ok("CONFIRMED");
            } else if ("ALREADY_PROCESSED".equals(result)) {
                return ResponseEntity.ok("ALREADY_PROCESSED");
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            LOG.error("Error processing VNPay webhook", e);
            return ResponseEntity.internalServerError().body("ERROR");
        }
    }

    /**
     * Handle VNPay query transaction status
     * This endpoint is used for reconciliation and manual transaction status
     * checking
     * Now gets transactionDate and orderRef from database instead of parameters
     */
    @GetMapping("/query/{transactionId}")
    public ResponseEntity<Map<String, Object>> queryTransaction(@PathVariable String transactionId,
            HttpServletRequest request) {
        LOG.debug("Querying VNPay transaction status: {}", transactionId);

        try {
            // Get transaction from database to obtain orderRef and transactionDate
            PaymentTransaction transaction = paymentTransactionRepository
                    .findByTransactionIdAndIsDeletedFalseOrIsDeletedIsNull(transactionId)
                    .orElse(null);

            if (transaction == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("transactionId", transactionId);
                errorResponse.put("success", false);
                errorResponse.put("responseCode", "01");
                errorResponse.put("message", "Transaction not found in database");
                errorResponse.put("paymentMethod", "VNPAY");
                errorResponse.put("queryTime", System.currentTimeMillis());
                errorResponse.put("canSynthesizeWebhook", false);
                return ResponseEntity.status(404).body(errorResponse);
            }

            String ipAddress = getClientIpAddress(request);
            String orderRef = transaction.getOrderRef();

            // Use new method that gets data from PaymentTransaction object
            VNPayService.VNPayQueryResult result = vnPayService.queryTransaction(transaction, ipAddress);

            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transactionId);
            response.put("orderRef", orderRef);
            response.put("success", result.isSuccess());
            response.put("responseCode", result.getResponseCode());
            response.put("message", result.getMessage());
            response.put("transactionStatus", result.getTransactionStatus());
            response.put("amount", result.getAmount());
            response.put("paymentMethod", "VNPAY");
            response.put("queryTime", System.currentTimeMillis());

            // Add additional fields for reconciliation
            if (result.isSuccess()) {
                response.put("canSynthesizeWebhook", true);
                Map<String, Object> reconciliationData = new HashMap<>();
                reconciliationData.put("gatewayStatus", result.getTransactionStatus());
                reconciliationData.put("amount", result.getAmount());
                reconciliationData.put("transactionId", transactionId);
                reconciliationData.put("orderRef", orderRef);
                reconciliationData.put("responseCode", result.getResponseCode());
                response.put("reconciliationData", reconciliationData);
                return ResponseEntity.ok(response);
            } else {
                response.put("canSynthesizeWebhook", false);
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            LOG.error("Error querying VNPay transaction: {}", transactionId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("transactionId", transactionId);
            errorResponse.put("success", false);
            errorResponse.put("responseCode", "99");
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            errorResponse.put("paymentMethod", "VNPAY");
            errorResponse.put("queryTime", System.currentTimeMillis());
            errorResponse.put("canSynthesizeWebhook", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Handle VNPay refund transaction
     */
    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<Map<String, Object>> refundTransaction(@PathVariable String transactionId,
            @RequestBody @Valid RefundRequestDTO refundRequest,
            HttpServletRequest request) {
        LOG.debug("Refunding VNPay transaction: {} with amount: {}", transactionId, refundRequest.getAmount());

        try {
            // Get transaction from database to obtain original transaction date
            PaymentTransaction transaction = paymentTransactionRepository
                    .findByTransactionIdAndIsDeletedFalseOrIsDeletedIsNull(transactionId)
                    .orElse(null);

            if (transaction == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("responseCode", "01");
                errorResponse.put("message", "Transaction not found in database");
                return ResponseEntity.status(404).body(errorResponse);
            }

            String ipAddress = getClientIpAddress(request);
            String originalTransDate = transaction.getGatewayCreateDate();

            // Validate that originalTransDate exists and is in correct format
            if (originalTransDate == null || originalTransDate.length() != 14) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("responseCode", "03");
                errorResponse.put("message", "Original transaction date is missing or invalid");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            VNPayService.VNPayRefundResult result = vnPayService.refundTransaction(
                    transactionId, refundRequest.getAmount(), ipAddress, refundRequest.getOrderInfo(),
                    refundRequest.getTransactionType(), originalTransDate);

            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transactionId);
            response.put("success", result.isSuccess());
            response.put("responseCode", result.getResponseCode());
            response.put("message", result.getMessage());
            response.put("transactionNo", result.getTransactionNo());
            response.put("transactionType", result.getTransactionType());
            response.put("transactionStatus", result.getTransactionStatus());

            if (result.isSuccess()) {
                // Save refund data to database
                try {
                    saveRefundData(transaction, refundRequest, result);
                } catch (Exception e) {
                    LOG.error("Error saving refund data for transaction: {}", transactionId, e);
                    // Don't fail the response if DB save fails, but log the error
                    response.put("dbSaveWarning", "Refund processed but failed to save to database");
                }
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            LOG.error("Error refunding VNPay transaction: {}", transactionId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("responseCode", "99");
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Manually trigger polling for a specific transaction
     * This endpoint is useful for testing and admin purposes when IPN is not
     * available
     */
    @PostMapping("/poll/{transactionId}")
    public ResponseEntity<Map<String, Object>> pollTransaction(@PathVariable String transactionId) {
        LOG.info("Manual polling triggered for transaction: {}", transactionId);

        try {
            boolean updated = vnPayPollingService.pollSpecificTransaction(transactionId);

            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transactionId);
            response.put("polled", true);
            response.put("updated", updated);
            response.put("timestamp", System.currentTimeMillis());

            if (updated) {
                response.put("message", "Transaction status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Transaction polled but no status change detected or transaction not found");
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            LOG.error("Error manually polling VNPay transaction: {}", transactionId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("transactionId", transactionId);
            errorResponse.put("polled", false);
            errorResponse.put("updated", false);
            errorResponse.put("message", "Error polling transaction: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Save refund data to database
     */
    private void saveRefundData(PaymentTransaction originalTransaction, RefundRequestDTO refundRequest,
            VNPayService.VNPayRefundResult refundResult) {
        LOG.info("Saving refund data for transaction: {}", originalTransaction.getTransactionId());

        // Check if transaction is already refunded
        if (PaymentStatus.REFUNDED.equals(originalTransaction.getStatus())) {
            LOG.warn("Transaction {} is already refunded", originalTransaction.getTransactionId());
            throw new IllegalStateException("Transaction is already refunded");
        }

        // Validate refund amount doesn't exceed original amount
        if (refundRequest.getAmount().compareTo(originalTransaction.getAmount()) > 0) {
            LOG.warn("Refund amount {} exceeds original transaction amount {}",
                    refundRequest.getAmount(), originalTransaction.getAmount());
            throw new IllegalArgumentException("Refund amount cannot exceed original transaction amount");
        }

        try {
            // Update original transaction status to REFUNDED
            originalTransaction.setStatus(PaymentStatus.REFUNDED);
            originalTransaction.setUpdatedAt(Instant.now());

            // Add refund information to gateway note
            String refundInfo = String.format(
                    "Refunded: %s - Amount: %s - RefundTxnNo: %s - RefundType: %s - RefundDate: %s",
                    refundResult.getTransactionNo() != null ? refundResult.getTransactionNo() : "N/A",
                    refundRequest.getAmount().toString(),
                    refundResult.getTransactionNo() != null ? refundResult.getTransactionNo() : "N/A",
                    refundRequest.getTransactionType(),
                    Instant.now().toString());

            String existingNote = originalTransaction.getGatewayNote();
            if (existingNote != null && !existingNote.trim().isEmpty()) {
                originalTransaction.setGatewayNote(existingNote + " | " + refundInfo);
            } else {
                originalTransaction.setGatewayNote(refundInfo);
            }

            // Save updated original transaction
            paymentTransactionRepository.save(originalTransaction);

            // Create new refund transaction record
            PaymentTransaction refundTransaction = new PaymentTransaction();
            refundTransaction.setTransactionId(
                    "REFUND-" + System.currentTimeMillis() + "-" + originalTransaction.getTransactionId());
            refundTransaction.setOrderRef(originalTransaction.getOrderRef());
            refundTransaction.setMethod(PaymentMethod.VNPAY);
            refundTransaction.setStatus(PaymentStatus.SUCCESS);
            refundTransaction.setAmount(refundRequest.getAmount());
            refundTransaction.setTime(Instant.now());
            refundTransaction.setGatewayCreateDate(VNPayUtils.getVNPayDate()); // Current date in VNPay format
            refundTransaction
                    .setGatewayNote(String.format("Refund for original transaction: %s | RefundTxnNo: %s | Type: %s",
                            originalTransaction.getTransactionId(),
                            refundResult.getTransactionNo() != null ? refundResult.getTransactionNo() : "N/A",
                            refundRequest.getTransactionType()));
            refundTransaction.setCreatedAt(Instant.now());
            refundTransaction.setUpdatedAt(Instant.now());

            // Save refund transaction
            paymentTransactionRepository.save(refundTransaction);

            LOG.info(
                    "Successfully saved refund data. Original transaction: {} updated to REFUNDED status. New refund transaction: {} created",
                    originalTransaction.getTransactionId(), refundTransaction.getTransactionId());

        } catch (Exception e) {
            LOG.error("Failed to save refund data for transaction: {}", originalTransaction.getTransactionId(), e);
            throw new RuntimeException("Failed to save refund data", e);
        }
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> handleVnpayIpn(HttpServletRequest request) {
        LOG.info("Received VNPay IPN webhook");

        try {
            // Extract all parameters from the request
            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((k, v) -> {
                if (v != null && v.length > 0) {
                    params.put(k, v[0]);
                }
            });

            LOG.info("===== VNPay IPN PARAMS =====");
            params.forEach((k, v) -> LOG.info("{} = {}", k, v));

            LOG.info("Raw QueryString: {}", request.getQueryString());

            // Validate VNPay signature first (according to VNPay IPN documentation)
            boolean isValidSignature = VNPayUtils.validateSignature(params, vnpayConfig.getHashSecret());

            if (!isValidSignature) {
                LOG.warn("Invalid VNPay IPN signature for transaction: {}", params.get("vnp_TxnRef"));
                return ResponseEntity.badRequest().body("{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}");
            }

            // Convert parameters to query string format for PaymentService
            String queryString = request.getQueryString();
            if (queryString != null) {
                // Process webhook using PaymentService
                String result = paymentService.processWebhook("VNPAY", queryString, null);

                if ("SUCCESS".equals(result)) {
                    return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                } else if ("ALREADY_PROCESSED".equals(result)) {
                    return ResponseEntity.ok("{\"RspCode\":\"02\",\"Message\":\"Order Already Processed\"}");
                } else if ("TRANSACTION_NOT_FOUND".equals(result)) {
                    return ResponseEntity.badRequest().body("{\"RspCode\":\"01\",\"Message\":\"Order not found\"}");
                } else if ("INVALID_AMOUNT".equals(result)) {
                    return ResponseEntity.badRequest().body("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                } else {
                    return ResponseEntity.badRequest().body("{\"RspCode\":\"99\",\"Message\":\"General Error\"}");
                }
            } else {
                return ResponseEntity.badRequest().body("{\"RspCode\":\"99\",\"Message\":\"No Data Received\"}");
            }

        } catch (Exception e) {
            LOG.error("Error processing VNPay IPN webhook", e);
            return ResponseEntity.internalServerError()
                    .body("{\"RspCode\":\"99\",\"Message\":\"Internal Server Error\"}");
        }
    }

}
