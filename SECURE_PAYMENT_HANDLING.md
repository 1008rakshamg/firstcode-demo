# Secure Razorpay Payment Handling Guide

This guide explains how to securely handle Razorpay payment success responses and save client data in your Spring Boot backend.

## 🔒 Security Overview

The secure payment handling system implements multiple layers of security:

1. **Signature Verification**: Validates Razorpay payment signatures
2. **Data Validation**: Ensures payment data integrity
3. **Duplicate Prevention**: Prevents double-processing of payments
4. **Error Handling**: Comprehensive error handling and logging
5. **Database Security**: Secure storage of payment information

## 🏗️ Architecture

```
Frontend (Razorpay Checkout) 
    ↓ (Payment Success)
Backend Verification Endpoint
    ↓ (Signature Verification)
Payment Service
    ↓ (Data Validation)
Database Storage
```

## 📋 Implementation Details

### 1. Payment Verification DTO

**File**: `RazorpayPaymentVerificationRequest.java`

```java
public class RazorpayPaymentVerificationRequest {
    private String razorpayPaymentId;    // Required
    private String razorpayOrderId;      // Required
    private String razorpaySignature;    // Required
    private String customerName;         // Optional
    private String customerEmail;        // Optional
    private String customerPhone;        // Optional
    private String description;          // Optional
}
```

### 2. Signature Verification Utility

**File**: `RazorpaySignatureVerifier.java`

- **HMAC SHA256**: Generates and verifies signatures
- **Data Validation**: Validates payment data integrity
- **Error Handling**: Comprehensive error handling

### 3. Secure Payment Service

**File**: `PaymentService.java`

**Method**: `handleSecurePaymentSuccess()`

**Steps**:
1. Verify payment signature
2. Find existing order in database
3. Check for duplicate processing
4. Update payment with client data
5. Save to database
6. Return verification result

### 4. API Endpoints

#### POST `/api/payments/razorpay/verify-payment`
- **Purpose**: Securely verify and save payment data
- **Request**: `RazorpayPaymentVerificationRequest`
- **Response**: `PaymentResponse`

#### GET `/api/payments/razorpay/payment/{paymentId}`
- **Purpose**: Retrieve payment by Razorpay payment ID
- **Response**: `PaymentResponse`

## 🔧 Frontend Integration

### Updated Payment Flow

```javascript
// 1. Create order via backend
const orderResponse = await fetch('/api/payments/razorpay/order', {
    method: 'POST',
    body: JSON.stringify({ amount: amountInPaise, currency: 'INR' })
});

// 2. Open Razorpay Checkout
const rzp = new Razorpay(options);
rzp.open();

// 3. Handle payment success with verification
handler: async function(response) {
    const verificationResponse = await fetch('/api/payments/razorpay/verify-payment', {
        method: 'POST',
        body: JSON.stringify({
            razorpayPaymentId: response.razorpay_payment_id,
            razorpayOrderId: response.razorpay_order_id,
            razorpaySignature: response.razorpay_signature,
            customerName: customerName,
            customerEmail: customerEmail,
            customerPhone: customerPhone,
            description: description
        })
    });
}
```

## 🛡️ Security Features

### 1. Signature Verification

```java
// Verify payment signature
boolean isSignatureValid = signatureVerifier.verifyPaymentSignature(
    request.getRazorpayPaymentId(),
    request.getRazorpayOrderId(),
    request.getRazorpaySignature()
);
```

### 2. Duplicate Payment Prevention

```java
// Check if payment already processed
if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
    return new PaymentResponse("Payment has already been processed successfully.");
}
```

### 3. Data Validation

```java
// Validate payment data integrity
if (!signatureVerifier.validatePaymentData(paymentId, orderId, amount, currency)) {
    return new PaymentResponse("Invalid payment data.");
}
```

### 4. Error Handling

- **Invalid Signature**: Returns error without processing
- **Order Not Found**: Validates order exists in system
- **Network Errors**: Graceful handling of communication failures
- **Database Errors**: Proper error logging and user feedback

## 📊 Database Schema

### Payment Entity Updates

The `Payment` entity includes fields for secure storage:

```java
@Entity
public class Payment {
    private String orderId;           // Our order ID
    private String paymentId;         // Razorpay payment ID
    private String customerName;      // Client data
    private String customerEmail;     // Client data
    private String customerPhone;     // Client data
    private BigDecimal amount;        // Payment amount
    private PaymentStatus status;     // Payment status
    private LocalDateTime capturedAt; // Payment capture time
    private String gatewayResponse;   // Raw gateway response
}
```

## 🧪 Testing

### Test Payment Flow

1. **Start Application**:
   ```bash
   cd PaymentGateway
   ./mvnw spring-boot:run
   ```

2. **Access Test Page**: http://localhost:8080/razorpay-test.html

3. **Complete Payment**:
   - Use test card: `4111 1111 1111 1111`
   - Any future expiry date
   - Any CVV and OTP

4. **Verify Results**:
   - Check browser console for logs
   - Verify payment in database
   - Check API responses

### Test Scenarios

#### ✅ Successful Payment with Verification
- Payment completes successfully
- Signature verification passes
- Data saved to database
- Success response returned

#### ❌ Invalid Signature
- Payment completes but verification fails
- Error response returned
- Payment not marked as successful

#### ⚠️ Duplicate Payment
- Payment already processed
- Error response returned
- No duplicate processing

#### 🔄 Network Error
- Payment completes but verification fails
- Error response returned
- User notified to contact support

## 🔍 Monitoring and Logging

### Console Logs

```java
// Payment verification logs
System.err.println("Error verifying Razorpay signature: " + e.getMessage());
System.err.println("Error processing secure payment success: " + e.getMessage());
```

### Database Queries

```sql
-- Check payment status
SELECT * FROM payments WHERE order_id = 'order_ABC123';

-- Verify payment by Razorpay payment ID
SELECT * FROM payments WHERE payment_id = 'pay_XYZ789';

-- Get payment statistics
SELECT status, COUNT(*) FROM payments GROUP BY status;
```

## 🚨 Production Considerations

### 1. Environment Variables

```properties
# Production Razorpay keys
razorpay.key.id=${RAZORPAY_KEY_ID}
razorpay.key.secret=${RAZORPAY_SECRET_KEY}
razorpay.webhook.secret=${RAZORPAY_WEBHOOK_SECRET}
```

### 2. HTTPS Enforcement

```java
// Ensure HTTPS in production
@Profile("prod")
@Configuration
public class SecurityConfig {
    // HTTPS configuration
}
```

### 3. Rate Limiting

```properties
# Rate limiting for payment endpoints
rate.limit.payment.requests-per-minute=10
rate.limit.payment.burst-capacity=20
```

### 4. Database Security

- Use encrypted database connections
- Implement database access controls
- Regular security audits
- Backup and recovery procedures

### 5. Logging and Monitoring

- Implement structured logging
- Set up monitoring alerts
- Regular security reviews
- Compliance reporting

## 📞 Support and Troubleshooting

### Common Issues

1. **Signature Verification Fails**
   - Check Razorpay secret key configuration
   - Verify payment data format
   - Check for encoding issues

2. **Payment Not Found**
   - Verify order ID exists in database
   - Check payment status
   - Review order creation process

3. **Database Errors**
   - Check database connectivity
   - Verify table schema
   - Review transaction logs

### Debug Mode

Enable debug logging in `application.properties`:

```properties
logging.level.com.payment=DEBUG
logging.level.com.razorpay=DEBUG
```

## 🔗 Related Documentation

- [Razorpay Order Creation API](./RAZORPAY_ORDER_API.md)
- [Razorpay Frontend Integration Guide](./RAZORPAY_FRONTEND_GUIDE.md)
- [Payment Gateway API Documentation](./README.md)

## 📝 API Reference

### Request Format

```json
{
  "razorpayPaymentId": "pay_ABC123XYZ",
  "razorpayOrderId": "order_DEF456UVW",
  "razorpaySignature": "abc123def456...",
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "+91-9876543210",
  "description": "Payment for services"
}
```

### Response Format

```json
{
  "success": true,
  "paymentId": "pay_ABC123XYZ",
  "orderId": "order_DEF456UVW",
  "amount": 1000.00,
  "currency": "INR",
  "status": "SUCCESS",
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "capturedAt": "2024-01-15T10:30:00"
}
```

This secure payment handling system ensures that all Razorpay payments are properly verified, validated, and stored securely in your database while providing comprehensive error handling and user feedback. 