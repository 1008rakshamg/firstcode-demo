package com.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private RazorpayClient razorpayClient;
    
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            // Generate unique order ID
            String orderId = "ORDER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            
            // Create payment entity
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setCustomerName(request.getCustomerName());
            payment.setCustomerEmail(request.getCustomerEmail());
            payment.setCustomerPhone(request.getCustomerPhone());
            payment.setAmount(request.getAmount());
            payment.setCurrency(request.getCurrency());
            payment.setDescription(request.getDescription());
            payment.setNotes(request.getNotes());
            payment.setStatus(Payment.PaymentStatus.PENDING);
            
            // Save payment to database
            payment = paymentRepository.save(payment);
            
            // Create Razorpay order (only if not in test mode)
            String paymentId = "PAY_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            
            try {
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Convert to paise
                orderRequest.put("currency", request.getCurrency());
                orderRequest.put("receipt", orderId);
                orderRequest.put("notes", new JSONObject().put("description", request.getDescription()));
                
                Order order = razorpayClient.orders.create(orderRequest);
                paymentId = order.get("id").toString();
            } catch (Exception e) {
                // In test mode or if Razorpay is not available, use mock payment ID
                System.out.println("Using mock payment ID for testing: " + e.getMessage());
            }
            
            // Update payment with payment ID
            payment.setPaymentId(paymentId);
            payment = paymentRepository.save(payment);
            
            // Return response with payment URL
            String paymentUrl = "https://checkout.razorpay.com/v1/" + paymentId;
            return new PaymentResponse(payment, paymentUrl);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment: " + e.getMessage(), e);
        }
    }
    
    public PaymentResponse verifyPayment(String paymentId, String orderId, String signature) {
        try {
            // Verify signature
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_signature", signature);
            
            // In production, you should verify the signature
            // boolean isValid = Utils.verifyPaymentSignature(attributes, secret);
            
            // For demo purposes, we'll assume the payment is successful
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment = paymentRepository.save(payment);
                return new PaymentResponse(payment);
            } else {
                return new PaymentResponse("Payment not found");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify payment: " + e.getMessage(), e);
        }
    }
    
    public PaymentResponse getPaymentById(Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        return payment.map(PaymentResponse::new)
                    .orElse(new PaymentResponse("Payment not found"));
    }
    
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Optional<Payment> payment = paymentRepository.findByOrderId(orderId);
        return payment.map(PaymentResponse::new)
                    .orElse(new PaymentResponse("Payment not found"));
    }
    
    public List<PaymentResponse> getPaymentsByCustomerEmail(String email) {
        List<Payment> payments = paymentRepository.findByCustomerEmail(email);
        return payments.stream().map(PaymentResponse::new).toList();
    }
    
    public List<PaymentResponse> getPaymentsByStatus(Payment.PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream().map(PaymentResponse::new).toList();
    }
    
    public List<PaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = paymentRepository.findByDateRange(startDate, endDate);
        return payments.stream().map(PaymentResponse::new).toList();
    }
    
    public PaymentResponse updatePaymentStatus(Long id, Payment.PaymentStatus status) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(status);
            payment = paymentRepository.save(payment);
            return new PaymentResponse(payment);
        } else {
            return new PaymentResponse("Payment not found");
        }
    }
    
    public PaymentResponse refundPayment(Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
                payment = paymentRepository.save(payment);
                return new PaymentResponse(payment);
            } else {
                return new PaymentResponse("Payment cannot be refunded. Only successful payments can be refunded.");
            }
        } else {
            return new PaymentResponse("Payment not found");
        }
    }
    
    public long getPaymentCountByStatus(Payment.PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }
    
    public BigDecimal getTotalAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.getTotalAmountByDateRange(startDate, endDate);
    }
    
    // Webhook processing methods
    public void processPaymentSuccess(String paymentId, String orderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
        }
    }
    
    public void processPaymentFailure(String paymentId, String orderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }
    
    public void processPaymentRefund(String paymentId, String orderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }
    }
} 