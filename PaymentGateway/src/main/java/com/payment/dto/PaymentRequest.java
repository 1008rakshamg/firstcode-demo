package com.payment.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PaymentRequest {
    
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Please provide a valid email address")
    private String customerEmail;
    
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Please provide a valid phone number")
    private String customerPhone;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1.0")
    @DecimalMax(value = "999999.99", message = "Amount cannot exceed 999999.99")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter code (e.g., USD, INR)")
    private String currency;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    // Additional fields for enhanced payment gateway
    private String merchantId;
    
    private String merchantOrderId;
    
    @Size(max = 100, message = "Return URL cannot exceed 100 characters")
    private String returnUrl;
    
    @Size(max = 100, message = "Cancel URL cannot exceed 100 characters")
    private String cancelUrl;
    
    @Size(max = 50, message = "Payment method cannot exceed 50 characters")
    private String paymentMethod;
    
    @Size(max = 50, message = "Bank code cannot exceed 50 characters")
    private String bankCode;
    
    @Size(max = 50, message = "Card network cannot exceed 50 characters")
    private String cardNetwork;
    
    @Size(max = 4, message = "Card last 4 digits cannot exceed 4 characters")
    private String cardLast4;
    
    @Size(max = 100, message = "Customer address cannot exceed 100 characters")
    private String customerAddress;
    
    @Size(max = 50, message = "Customer city cannot exceed 50 characters")
    private String customerCity;
    
    @Size(max = 50, message = "Customer state cannot exceed 50 characters")
    private String customerState;
    
    @Size(max = 20, message = "Customer postal code cannot exceed 20 characters")
    private String customerPostalCode;
    
    @Size(max = 50, message = "Customer country cannot exceed 50 characters")
    private String customerCountry;
    
    private Boolean autoCapture = true;
    
    private Integer timeoutSeconds = 300;
    
    // Default constructor
    public PaymentRequest() {}
    
    // Constructor with all fields
    public PaymentRequest(String customerName, String customerEmail, String customerPhone, 
                         BigDecimal amount, String currency, String description, String notes) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.notes = notes;
    }
    
    // Getters and Setters
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
    
    public String getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
    
    public String getMerchantOrderId() {
        return merchantOrderId;
    }
    
    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public String getCancelUrl() {
        return cancelUrl;
    }
    
    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getBankCode() {
        return bankCode;
    }
    
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    
    public String getCardNetwork() {
        return cardNetwork;
    }
    
    public void setCardNetwork(String cardNetwork) {
        this.cardNetwork = cardNetwork;
    }
    
    public String getCardLast4() {
        return cardLast4;
    }
    
    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }
    
    public String getCustomerAddress() {
        return customerAddress;
    }
    
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
    
    public String getCustomerCity() {
        return customerCity;
    }
    
    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }
    
    public String getCustomerState() {
        return customerState;
    }
    
    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }
    
    public String getCustomerPostalCode() {
        return customerPostalCode;
    }
    
    public void setCustomerPostalCode(String customerPostalCode) {
        this.customerPostalCode = customerPostalCode;
    }
    
    public String getCustomerCountry() {
        return customerCountry;
    }
    
    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }
    
    public Boolean getAutoCapture() {
        return autoCapture;
    }
    
    public void setAutoCapture(Boolean autoCapture) {
        this.autoCapture = autoCapture;
    }
    
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
} 