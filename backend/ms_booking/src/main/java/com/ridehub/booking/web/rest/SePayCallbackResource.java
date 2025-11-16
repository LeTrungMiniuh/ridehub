package com.ridehub.booking.web.rest;

import com.ridehub.booking.service.PaymentService;
import com.ridehub.booking.service.payment.sepay.SePayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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

    public SePayCallbackResource(PaymentService paymentService, SePayService sePayService) {
        this.paymentService = paymentService;
        this.sePayService = sePayService;
    }

    /**
     * Handle SePay return callback (user redirected back to merchant site)
     */
    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(HttpServletRequest request) {
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

            Map<String, Object> response = new HashMap<>();
            if (result.isValid()) {
                response.put("status", "success");
                response.put("message", result.getMessage());
                response.put("transactionId", result.getTransactionId());
                response.put("paymentStatus", result.getStatus());
                
                LOG.info("SePay callback verified successfully for transaction: {}", result.getTransactionId());
            } else {
                response.put("status", "error");
                response.put("message", result.getMessage());
                
                LOG.warn("SePay callback verification failed: {}", result.getMessage());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOG.error("Error processing SePay callback", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Handle SePay IPN (Instant Payment Notification) webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader(value = "X-Signature", required = false) String signature) {
        LOG.debug("Received SePay webhook");

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