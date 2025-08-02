package com.example.paymentgateway.service;

import com.example.paymentgateway.dto.PaymentRequest;
import com.example.paymentgateway.dto.PaymentResponse;
import com.example.paymentgateway.model.PaymentOrder;
import com.example.paymentgateway.repository.PaymentOrderRepository;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentOrderRepository paymentOrderRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public PaymentResponse createOrder(PaymentRequest request) {
        try {
            log.info("Creating payment order for amount: {} {}", request.getAmount(), request.getCurrency());

            // Create Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", "receipt_" + UUID.randomUUID().toString().substring(0, 8));

            // Add customer notes
            JSONObject notes = new JSONObject();
            notes.put("customer_name", request.getCustomerName());
            notes.put("customer_email", request.getCustomerEmail());
            if (request.getCustomerPhone() != null) {
                notes.put("customer_phone", request.getCustomerPhone());
            }
            if (request.getNotes() != null) {
                notes.put("notes", request.getNotes());
            }
            orderRequest.put("notes", notes);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            log.info("Razorpay order created with ID: {}", razorpayOrder.get("id").toString());

            // Save order to database
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setRazorpayOrderId(razorpayOrder.get("id"));
            paymentOrder.setAmount(request.getAmount());
            paymentOrder.setCurrency(request.getCurrency());
            paymentOrder.setReceipt(razorpayOrder.get("receipt"));
            paymentOrder.setCustomerName(request.getCustomerName());
            paymentOrder.setCustomerEmail(request.getCustomerEmail());
            paymentOrder.setCustomerPhone(request.getCustomerPhone());
            paymentOrder.setNotes(request.getNotes());
            paymentOrder.setStatus(PaymentOrder.PaymentStatus.CREATED);

            paymentOrder = paymentOrderRepository.save(paymentOrder);

            return PaymentResponse.builder()
                    .orderId(paymentOrder.getId().toString())
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .customerName(request.getCustomerName())
                    .customerEmail(request.getCustomerEmail())
                    .customerPhone(request.getCustomerPhone())
                    .status(paymentOrder.getStatus().toString())
                    .notes(request.getNotes())
                    .razorpayKeyId(keyId)
                    .success(true)
                    .message("Order created successfully")
                    .build();

        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order: {}", e.getMessage(), e);
            return PaymentResponse.builder()
                    .success(false)
                    .error("Failed to create payment order: " + e.getMessage())
                    .build();
        }
    }

    public PaymentResponse verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            log.info("Verifying payment for order: {}", razorpayOrderId);

            // Find the order in database
            Optional<PaymentOrder> orderOpt = paymentOrderRepository.findByRazorpayOrderId(razorpayOrderId);
            if (orderOpt.isEmpty()) {
                return PaymentResponse.builder()
                        .success(false)
                        .error("Order not found")
                        .build();
            }

            PaymentOrder paymentOrder = orderOpt.get();

            // Verify signature
            boolean isValidSignature = verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            
            if (isValidSignature) {
                // Update payment order
                paymentOrder.setRazorpayPaymentId(razorpayPaymentId);
                paymentOrder.setRazorpaySignature(razorpaySignature);
                paymentOrder.setStatus(PaymentOrder.PaymentStatus.PAID);
                paymentOrderRepository.save(paymentOrder);

                log.info("Payment verified successfully for order: {}", razorpayOrderId);

                return PaymentResponse.builder()
                        .orderId(paymentOrder.getId().toString())
                        .razorpayOrderId(razorpayOrderId)
                        .amount(paymentOrder.getAmount())
                        .currency(paymentOrder.getCurrency())
                        .customerName(paymentOrder.getCustomerName())
                        .customerEmail(paymentOrder.getCustomerEmail())
                        .status(paymentOrder.getStatus().toString())
                        .success(true)
                        .message("Payment verified successfully")
                        .build();
            } else {
                paymentOrder.setStatus(PaymentOrder.PaymentStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);

                return PaymentResponse.builder()
                        .success(false)
                        .error("Payment verification failed")
                        .build();
            }

        } catch (Exception e) {
            log.error("Error verifying payment: {}", e.getMessage(), e);
            return PaymentResponse.builder()
                    .success(false)
                    .error("Payment verification failed: " + e.getMessage())
                    .build();
        }
    }

    public List<PaymentOrder> getAllOrders() {
        return paymentOrderRepository.findAll();
    }

    public Optional<PaymentOrder> getOrderById(Long id) {
        return paymentOrderRepository.findById(id);
    }

    public Optional<PaymentOrder> getOrderByRazorpayOrderId(String razorpayOrderId) {
        return paymentOrderRepository.findByRazorpayOrderId(razorpayOrderId);
    }

    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature = calculateHMAC(payload, keySecret);
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage(), e);
            return false;
        }
    }

    private String calculateHMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
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
    }
}