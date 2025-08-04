package com.payment.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for verifying Razorpay payment signatures
 * This ensures that payment responses are authentic and not tampered with
 */
@Component
public class RazorpaySignatureVerifier {
    
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;
    
    /**
     * Verify Razorpay payment signature
     * 
     * @param paymentId Razorpay payment ID
     * @param orderId Razorpay order ID
     * @param signature Razorpay signature
     * @return true if signature is valid, false otherwise
     */
    public boolean verifyPaymentSignature(String paymentId, String orderId, String signature) {
        try {
            // Create the expected signature
            String expectedSignature = generateSignature(paymentId + "|" + orderId);
            
            // Compare signatures
            return expectedSignature.equals(signature);
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error verifying Razorpay signature: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verify webhook signature
     * 
     * @param payload Raw webhook payload
     * @param signature Webhook signature
     * @return true if signature is valid, false otherwise
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            String expectedSignature = generateSignature(payload);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            System.err.println("Error verifying webhook signature: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate HMAC SHA256 signature for testing purposes
     * 
     * @param data Data to sign
     * @return HMAC SHA256 signature
     */
    public String generateSignature(String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                razorpaySecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            System.err.println("Error generating signature: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate payment data integrity
     * 
     * @param paymentId Payment ID
     * @param orderId Order ID
     * @param amount Amount in paise
     * @param currency Currency code
     * @return true if data is valid, false otherwise
     */
    public boolean validatePaymentData(String paymentId, String orderId, Integer amount, String currency) {
        // Basic validation
        if (paymentId == null || paymentId.trim().isEmpty()) {
            return false;
        }
        
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }
        
        if (amount == null || amount <= 0) {
            return false;
        }
        
        if (currency == null || currency.trim().isEmpty() || currency.length() != 3) {
            return false;
        }
        
        return true;
    }
} 