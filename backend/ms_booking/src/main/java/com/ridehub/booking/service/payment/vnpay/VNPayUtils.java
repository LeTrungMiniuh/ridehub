package com.ridehub.booking.service.payment.vnpay;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * VNPay utility class for handling payment operations
 */
public class VNPayUtils {
    
    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String VIETNAM_TIMEZONE = "Asia/Ho_Chi_Minh";
    
    /**
     * Generate HMAC SHA512 hash
     */
    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA512);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating HMAC SHA512", e);
        }
    }
    
    /**
     * Convert byte array to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Build query string from parameters
     */
    public static String buildQuery(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    query.append('&');
                }
            }
        }
        return query.toString();
    }
    
    /**
     * Generate VNPay date format (yyyyMMddHHmmss)
     */
    public static String getVNPayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone(VIETNAM_TIMEZONE));
        return formatter.format(new Date());
    }
    
    /**
     * Generate VNPay expire date (yyyyMMddHHmmss) - 5 minutes from now
     */
    public static String getVNPayExpireDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(VIETNAM_TIMEZONE));
        calendar.add(Calendar.MINUTE, 5);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone(VIETNAM_TIMEZONE));
        return formatter.format(calendar.getTime());
    }
    
    /**
     * Generate VNPay expire date (yyyyMMddHHmmss) - 5 minutes from the given instant
     */
    public static String getVNPayExpireDate(Instant bookingExpiresAt) {
        ZonedDateTime expireDateTime = bookingExpiresAt.atZone(ZoneId.of(VIETNAM_TIMEZONE));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone(VIETNAM_TIMEZONE));
        return formatter.format(Date.from(expireDateTime.toInstant()));
    }
    
    /**
     * Validate VNPay response signature
     */
    public static boolean validateSignature(Map<String, String> params, String secretKey) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        params.remove("vnp_SecureHash");
        
        String signValue = buildQuery(params);
        String checkSum = hmacSHA512(secretKey, signValue);
        
        return checkSum.equals(vnpSecureHash);
    }
    
    /**
     * Parse query string to map
     */
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}
