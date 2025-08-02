package com.payment.util;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Pattern;

import com.payment.exception.PaymentException;

public class PaymentUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    
    public static String generateOrderId() {
        return "ORDER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    public static String generatePaymentId() {
        return "PAY_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    public static String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public static boolean isValidCurrency(String currency) {
        return currency != null && currency.length() == 3 && currency.matches("[A-Z]{3}");
    }
    
    public static void validatePaymentRequest(String customerName, String customerEmail, 
                                           String customerPhone, BigDecimal amount, String currency) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new PaymentException("Customer name is required");
        }
        
        if (!isValidEmail(customerEmail)) {
            throw new PaymentException("Invalid email format");
        }
        
        if (!isValidPhone(customerPhone)) {
            throw new PaymentException("Invalid phone number format");
        }
        
        if (!isValidAmount(amount)) {
            throw new PaymentException("Invalid amount");
        }
        
        if (!isValidCurrency(currency)) {
            throw new PaymentException("Invalid currency format");
        }
    }
    
    public static BigDecimal convertToSmallestUnit(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)); // Convert to paise for INR
    }
    
    public static BigDecimal convertFromSmallestUnit(BigDecimal amount) {
        return amount.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < cardNumber.length() - 4; i++) {
            masked.append("*");
        }
        return masked.toString() + cardNumber.substring(cardNumber.length() - 4);
    }
    
    public static String formatAmount(BigDecimal amount, String currency) {
        return String.format("%s %.2f", currency, amount);
    }
} 