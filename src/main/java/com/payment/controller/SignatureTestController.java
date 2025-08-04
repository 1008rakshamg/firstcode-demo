package com.payment.controller;

import com.payment.util.RazorpaySignatureVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for demonstrating Razorpay signature verification
 * This is for testing and debugging purposes only
 */
@RestController
@RequestMapping("/api/test")
public class SignatureTestController {
    
    @Autowired
    private RazorpaySignatureVerifier signatureVerifier;
    
    /**
     * Test signature verification with provided parameters
     */
    @PostMapping("/verify-signature")
    public ResponseEntity<Map<String, Object>> testSignatureVerification(@RequestBody Map<String, String> request) {
        String paymentId = request.get("paymentId");
        String orderId = request.get("orderId");
        String signature = request.get("signature");
        
        // Validate input
        if (paymentId == null || orderId == null || signature == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Missing required parameters: paymentId, orderId, signature");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Verify signature
        boolean isValid = signatureVerifier.verifyPaymentSignature(paymentId, orderId, signature);
        
        // Generate expected signature for comparison
        String expectedSignature = signatureVerifier.generateSignature(paymentId + "|" + orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("paymentId", paymentId);
        response.put("orderId", orderId);
        response.put("providedSignature", signature);
        response.put("expectedSignature", expectedSignature);
        response.put("dataString", paymentId + "|" + orderId);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate a signature for testing purposes
     */
    @PostMapping("/generate-signature")
    public ResponseEntity<Map<String, Object>> generateSignature(@RequestBody Map<String, String> request) {
        String paymentId = request.get("paymentId");
        String orderId = request.get("orderId");
        
        if (paymentId == null || orderId == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Missing required parameters: paymentId, orderId");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        String dataString = paymentId + "|" + orderId;
        String signature = signatureVerifier.generateSignature(dataString);
        
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", paymentId);
        response.put("orderId", orderId);
        response.put("dataString", dataString);
        response.put("signature", signature);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test webhook signature verification
     */
    @PostMapping("/verify-webhook-signature")
    public ResponseEntity<Map<String, Object>> testWebhookSignatureVerification(@RequestBody Map<String, String> request) {
        String payload = request.get("payload");
        String signature = request.get("signature");
        
        if (payload == null || signature == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Missing required parameters: payload, signature");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        boolean isValid = signatureVerifier.verifyWebhookSignature(payload, signature);
        String expectedSignature = signatureVerifier.generateSignature(payload);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("payload", payload);
        response.put("providedSignature", signature);
        response.put("expectedSignature", expectedSignature);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check for signature verifier
     */
    @GetMapping("/signature-verifier-health")
    public ResponseEntity<Map<String, Object>> signatureVerifierHealth() {
        try {
            // Test signature verification with known data
            String testPaymentId = "pay_test123";
            String testOrderId = "order_test456";
            String testData = testPaymentId + "|" + testOrderId;
            
            // Generate a signature
            String testSignature = signatureVerifier.generateSignature(testData);
            
            // Verify the signature
            boolean isValid = signatureVerifier.verifyPaymentSignature(testPaymentId, testOrderId, testSignature);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", isValid ? "UP" : "DOWN");
            response.put("component", "RazorpaySignatureVerifier");
            response.put("testPaymentId", testPaymentId);
            response.put("testOrderId", testOrderId);
            response.put("testSignature", testSignature);
            response.put("verificationResult", isValid);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("component", "RazorpaySignatureVerifier");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(503).body(response);
        }
    }
    
    /**
     * Get signature verification information
     */
    @GetMapping("/signature-info")
    public ResponseEntity<Map<String, Object>> getSignatureInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "HMAC SHA256");
        response.put("dataFormat", "paymentId|orderId");
        response.put("description", "Razorpay payment signature verification");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
} 