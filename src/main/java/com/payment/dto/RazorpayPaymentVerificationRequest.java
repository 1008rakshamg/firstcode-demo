package com.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RazorpayPaymentVerificationRequest {
    
    @NotBlank(message = "Payment ID is required")
    private String razorpayPaymentId;
    
    @NotBlank(message = "Order ID is required")
    private String razorpayOrderId;
    
    @NotBlank(message = "Signature is required")
    private String razorpaySignature;
    
    // Additional client data
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String description;
    
    // Default constructor
    public RazorpayPaymentVerificationRequest() {}
    
    // Constructor with required fields
    public RazorpayPaymentVerificationRequest(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpaySignature = razorpaySignature;
    }
    
    // Constructor with all fields
    public RazorpayPaymentVerificationRequest(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature,
                                             String customerName, String customerEmail, String customerPhone, String description) {
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpaySignature = razorpaySignature;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.description = description;
    }
    
    // Getters and Setters
    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }
    
    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }
    
    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }
    
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }
    
    public String getRazorpaySignature() {
        return razorpaySignature;
    }
    
    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "RazorpayPaymentVerificationRequest{" +
                "razorpayPaymentId='" + razorpayPaymentId + '\'' +
                ", razorpayOrderId='" + razorpayOrderId + '\'' +
                ", razorpaySignature='" + razorpaySignature + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 