package com.example.paymentgateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String orderId;
    private String razorpayOrderId;
    private BigDecimal amount;
    private String currency;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String status;
    private String notes;
    private String razorpayKeyId;
    
    // For success response
    private boolean success;
    private String message;
    
    // For error response
    private String error;
}