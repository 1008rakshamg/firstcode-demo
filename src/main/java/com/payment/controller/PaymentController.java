package com.payment.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.RazorpayPaymentVerificationRequest;
import com.payment.entity.Payment;
import com.payment.service.PaymentService;
import com.razorpay.RazorpayClient;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RazorpayClient razorpayClient;
    
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.createPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentResponse("Failed to create payment: " + e.getMessage()));
        }
    }
    
    @PostMapping("/razorpay/order")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(@RequestBody Map<String, Object> request) {
        try {
            // Extract amount from request (amount should be in paise for Razorpay)
            Integer amount = (Integer) request.get("amount");
            String currency = (String) request.getOrDefault("currency", "INR");
            String receipt = (String) request.getOrDefault("receipt", "receipt_" + System.currentTimeMillis());
            
            if (amount == null || amount <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid amount. Amount must be greater than 0 and in paise.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Create JSON object for Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            
            // Create order using Razorpay client
            Order order = razorpayClient.orders.create(orderRequest);
            
            // Return order details
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("receipt", order.get("receipt"));
            response.put("status", order.get("status"));
            response.put("createdAt", order.get("created_at"));
            
            return ResponseEntity.ok(response);
            
        } catch (RazorpayException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create Razorpay order: " + e.getMessage());
            errorResponse.put("errorCode", e.getCode());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @RequestParam String paymentId,
            @RequestParam String orderId,
            @RequestParam String signature) {
        try {
            PaymentResponse response = paymentService.verifyPayment(paymentId, orderId, signature);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentResponse("Failed to verify payment: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        if (response.getMessage() != null && response.getMessage().equals("Payment not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable String orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        if (response.getMessage() != null && response.getMessage().equals("Payment not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomerEmail(@PathVariable String email) {
        List<PaymentResponse> responses = paymentService.getPaymentsByCustomerEmail(email);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable Payment.PaymentStatus status) {
        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<PaymentResponse> responses = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Payment.PaymentStatus status) {
        PaymentResponse response = paymentService.updatePaymentStatus(id, status);
        if (response.getMessage() != null && response.getMessage().equals("Payment not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        PaymentResponse response = paymentService.refundPayment(id);
        if (response.getMessage() != null && response.getMessage().equals("Payment not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats/count/{status}")
    public ResponseEntity<Long> getPaymentCountByStatus(@PathVariable Payment.PaymentStatus status) {
        long count = paymentService.getPaymentCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/total-amount")
    public ResponseEntity<BigDecimal> getTotalAmountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal totalAmount = paymentService.getTotalAmountByDateRange(startDate, endDate);
        return ResponseEntity.ok(totalAmount != null ? totalAmount : BigDecimal.ZERO);
    }
    
    @GetMapping("/stats/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get counts for each status
        stats.put("pending", paymentService.getPaymentCountByStatus(Payment.PaymentStatus.PENDING));
        stats.put("success", paymentService.getPaymentCountByStatus(Payment.PaymentStatus.SUCCESS));
        stats.put("failed", paymentService.getPaymentCountByStatus(Payment.PaymentStatus.FAILED));
        stats.put("cancelled", paymentService.getPaymentCountByStatus(Payment.PaymentStatus.CANCELLED));
        stats.put("refunded", paymentService.getPaymentCountByStatus(Payment.PaymentStatus.REFUNDED));
        
        // Get total amount for last 30 days
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        BigDecimal totalAmount = paymentService.getTotalAmountByDateRange(startDate, endDate);
        stats.put("totalAmountLast30Days", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Gateway is running!");
    }
    
    @PostMapping("/razorpay/verify-payment")
    public ResponseEntity<PaymentResponse> verifyRazorpayPayment(@Valid @RequestBody RazorpayPaymentVerificationRequest request) {
        try {
            PaymentResponse response = paymentService.handleSecurePaymentSuccess(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentResponse("Failed to verify payment: " + e.getMessage()));
        }
    }
    
    @GetMapping("/razorpay/payment/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentByRazorpayPaymentId(@PathVariable String paymentId) {
        PaymentResponse response = paymentService.getPaymentByRazorpayPaymentId(paymentId);
        if (response.getMessage() != null && response.getMessage().equals("Payment not found")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api-docs")
    public ResponseEntity<Map<String, Object>> getApiDocumentation() {
        Map<String, Object> apiDocs = new HashMap<>();
        
        Map<String, Object> endpoints = new HashMap<>();
        
        // POST endpoints
        Map<String, Object> createPayment = new HashMap<>();
        createPayment.put("method", "POST");
        createPayment.put("url", "/api/payments/create");
        createPayment.put("description", "Create a new payment");
        createPayment.put("body", "PaymentRequest object");
        endpoints.put("createPayment", createPayment);
        
        Map<String, Object> verifyPayment = new HashMap<>();
        verifyPayment.put("method", "POST");
        verifyPayment.put("url", "/api/payments/verify");
        verifyPayment.put("description", "Verify a payment");
        verifyPayment.put("params", "paymentId, orderId, signature");
        endpoints.put("verifyPayment", verifyPayment);
        
        Map<String, Object> verifyRazorpayPayment = new HashMap<>();
        verifyRazorpayPayment.put("method", "POST");
        verifyRazorpayPayment.put("url", "/api/payments/razorpay/verify-payment");
        verifyRazorpayPayment.put("description", "Securely verify Razorpay payment and save client data");
        verifyRazorpayPayment.put("body", "RazorpayPaymentVerificationRequest object");
        endpoints.put("verifyRazorpayPayment", verifyRazorpayPayment);
        
        // GET endpoints
        Map<String, Object> getPaymentById = new HashMap<>();
        getPaymentById.put("method", "GET");
        getPaymentById.put("url", "/api/payments/{id}");
        getPaymentById.put("description", "Get payment by ID");
        endpoints.put("getPaymentById", getPaymentById);
        
        Map<String, Object> getPaymentByOrderId = new HashMap<>();
        getPaymentByOrderId.put("method", "GET");
        getPaymentByOrderId.put("url", "/api/payments/order/{orderId}");
        getPaymentByOrderId.put("description", "Get payment by order ID");
        endpoints.put("getPaymentByOrderId", getPaymentByOrderId);
        
        Map<String, Object> getPaymentByRazorpayPaymentId = new HashMap<>();
        getPaymentByRazorpayPaymentId.put("method", "GET");
        getPaymentByRazorpayPaymentId.put("url", "/api/payments/razorpay/payment/{paymentId}");
        getPaymentByRazorpayPaymentId.put("description", "Get payment by Razorpay payment ID");
        endpoints.put("getPaymentByRazorpayPaymentId", getPaymentByRazorpayPaymentId);
        
        Map<String, Object> getPaymentsByCustomer = new HashMap<>();
        getPaymentsByCustomer.put("method", "GET");
        getPaymentsByCustomer.put("url", "/api/payments/customer/{email}");
        getPaymentsByCustomer.put("description", "Get all payments for a customer");
        endpoints.put("getPaymentsByCustomer", getPaymentsByCustomer);
        
        Map<String, Object> getPaymentsByStatus = new HashMap<>();
        getPaymentsByStatus.put("method", "GET");
        getPaymentsByStatus.put("url", "/api/payments/status/{status}");
        getPaymentsByStatus.put("description", "Get payments by status");
        endpoints.put("getPaymentsByStatus", getPaymentsByStatus);
        
        Map<String, Object> getPaymentsByDateRange = new HashMap<>();
        getPaymentsByDateRange.put("method", "GET");
        getPaymentsByDateRange.put("url", "/api/payments/date-range");
        getPaymentsByDateRange.put("description", "Get payments by date range");
        getPaymentsByDateRange.put("params", "startDate, endDate");
        endpoints.put("getPaymentsByDateRange", getPaymentsByDateRange);
        
        // PUT endpoints
        Map<String, Object> updatePaymentStatus = new HashMap<>();
        updatePaymentStatus.put("method", "PUT");
        updatePaymentStatus.put("url", "/api/payments/{id}/status");
        updatePaymentStatus.put("description", "Update payment status");
        updatePaymentStatus.put("params", "status");
        endpoints.put("updatePaymentStatus", updatePaymentStatus);
        
        // POST endpoints for actions
        Map<String, Object> refundPayment = new HashMap<>();
        refundPayment.put("method", "POST");
        refundPayment.put("url", "/api/payments/{id}/refund");
        refundPayment.put("description", "Refund a payment");
        endpoints.put("refundPayment", refundPayment);
        
        // Stats endpoints
        Map<String, Object> getPaymentCount = new HashMap<>();
        getPaymentCount.put("method", "GET");
        getPaymentCount.put("url", "/api/payments/stats/count/{status}");
        getPaymentCount.put("description", "Get payment count by status");
        endpoints.put("getPaymentCount", getPaymentCount);
        
        Map<String, Object> getTotalAmount = new HashMap<>();
        getTotalAmount.put("method", "GET");
        getTotalAmount.put("url", "/api/payments/stats/total-amount");
        getTotalAmount.put("description", "Get total amount by date range");
        getTotalAmount.put("params", "startDate, endDate");
        endpoints.put("getTotalAmount", getTotalAmount);
        
        Map<String, Object> getDashboardStats = new HashMap<>();
        getDashboardStats.put("method", "GET");
        getDashboardStats.put("url", "/api/payments/stats/dashboard");
        getDashboardStats.put("description", "Get dashboard statistics");
        endpoints.put("getDashboardStats", getDashboardStats);
        
        apiDocs.put("endpoints", endpoints);
        apiDocs.put("baseUrl", "/api/payments");
        apiDocs.put("version", "1.0");
        apiDocs.put("description", "Payment Gateway REST API");
        
        return ResponseEntity.ok(apiDocs);
    }
}
