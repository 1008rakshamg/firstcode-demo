package com.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.payment.entity.Payment;

public class PaymentResponse {
    
    private Long id;
    private String orderId;
    private String paymentId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal amount;
    private String currency;
    private Payment.PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private String notes;
    private String paymentUrl;
    private String message;
    
    // Default constructor
    public PaymentResponse() {}
    
    // Constructor from Payment entity
    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.orderId = payment.getOrderId();
        this.paymentId = payment.getPaymentId();
        this.customerName = payment.getCustomerName();
        this.customerEmail = payment.getCustomerEmail();
        this.customerPhone = payment.getCustomerPhone();
        this.amount = payment.getAmount();
        this.currency = payment.getCurrency();
        this.status = payment.getStatus();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
        this.description = payment.getDescription();
        this.notes = payment.getNotes();
    }
    
    // Constructor with payment URL for new payments
    public PaymentResponse(Payment payment, String paymentUrl) {
        this(payment);
        this.paymentUrl = paymentUrl;
    }
    
    // Constructor for error responses
    public PaymentResponse(String message) {
        this.message = message;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Payment.PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(Payment.PaymentStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getPaymentUrl() {
        return paymentUrl;
    }
    
    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 