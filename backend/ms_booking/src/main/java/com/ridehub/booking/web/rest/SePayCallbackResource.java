package com.ridehub.booking.web.rest;

import com.ridehub.booking.service.PaymentService;
import com.ridehub.booking.service.payment.sepay.SePayConfig;
import com.ridehub.booking.service.payment.sepay.SePayService;
import com.ridehub.booking.service.payment.sepay.SePayService.SePayOrderDetail;
import com.ridehub.booking.service.payment.vnpay.VNPayConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for handling SePay callbacks and webhooks.
 */
@RestController
@RequestMapping("/api/payment/sepay")
public class SePayCallbackResource {

    private static final Logger LOG = LoggerFactory.getLogger(SePayCallbackResource.class);

    private final PaymentService paymentService;
    private final SePayService sePayService;
    private final SePayConfig sePayConfig;

    public SePayCallbackResource(PaymentService paymentService, SePayService sePayService, SePayConfig sePayConfig) {
        this.paymentService = paymentService;
        this.sePayService = sePayService;
        this.sePayConfig = sePayConfig;
    }

    /**
     * Handle SePay return callback (user redirected back to merchant site)
     */
    @GetMapping("/callback")
    public void handleCallback(HttpServletRequest request, HttpServletResponse response) {

        LOG.debug("Received SePay callback");

        try {
            // Extract all parameters from the request
            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                if (values.length > 0) {
                    params.put(key, values[0]);
                }
            });

            // Verify the callback
            SePayService.SePayCallbackResult result = sePayService.verifyCallback(params);

            String base = sePayConfig.getReturnFEURL();

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
                response.sendRedirect(sePayConfig.getReturnFEURL() + "?status=error&message=server_error");
            } catch (Exception ignore) {
            }
        }
    }

    @GetMapping("/success")
    public void handleSuccess(@RequestParam Map<String, String> params, HttpServletResponse response)
            throws IOException {
        LOG.info("SePay SUCCESS redirect received: {}", params);

        String base = sePayConfig.getReturnFEURL();

        String redirectUrl = base
                + "?status=success"
                + "&message=" + URLEncoder.encode("Payment successful", StandardCharsets.UTF_8)
                + "&transactionId="
                + URLEncoder.encode(params.getOrDefault("order_invoice_number", ""), StandardCharsets.UTF_8)
                + "&paymentStatus=SUCCESS";

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/error")
    public void handleError(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        LOG.info("SePay ERROR redirect received: {}", params);

        String base = sePayConfig.getReturnFEURL();

        String redirectUrl = base
                + "?status=error"
                + "&message="
                + URLEncoder.encode(params.getOrDefault("message", "Payment error"), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }

    /**
     * Handle SePay IPN (Instant Payment Notification) webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {
        LOG.info("Received SePay webhook");

        try {
            String result = paymentService.processWebhook("SEPAY", payload, signature);

            if ("SUCCESS".equals(result)) {
                return ResponseEntity.ok("CONFIRMED");
            } else if ("ALREADY_PROCESSED".equals(result)) {
                return ResponseEntity.ok("ALREADY_PROCESSED");
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            LOG.error("Error processing SePay webhook", e);
            return ResponseEntity.internalServerError().body("ERROR");
        }
    }

    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> queryOrderDetail(@RequestParam("orderId") String transactionId) {
        LOG.debug("REST request to query SePay order detail for orderId: {}", transactionId);

        if (transactionId == null || transactionId.isBlank()) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "orderId is required");
            return ResponseEntity.badRequest().body(errorBody);
        }

        try {
            SePayOrderDetail detail = sePayService.queryOrderDetail(transactionId);

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("orderId", transactionId);
            body.put("data", detail);

            return ResponseEntity.ok(body);
        } catch (RuntimeException ex) {
            LOG.error("Error querying SePay order detail for orderId {}", transactionId, ex);

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("orderId", transactionId);
            errorBody.put("message", ex.getMessage());

            // 502 because it's basically an upstream (SePay) failure
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorBody);
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
}