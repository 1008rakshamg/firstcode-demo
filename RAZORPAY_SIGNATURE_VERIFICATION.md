# Razorpay Payment Signature Verification Guide

This guide demonstrates how to verify Razorpay payment signatures using Java in Spring Boot to ensure payment authenticity and security.

## 🔒 Why Signature Verification is Important

Razorpay uses HMAC SHA256 signatures to verify that payment responses are authentic and haven't been tampered with. This prevents:
- **Replay Attacks**: Malicious users replaying payment responses
- **Data Tampering**: Modification of payment data in transit
- **Fake Payments**: Fraudulent payment confirmations

## 🏗️ Architecture Overview

```
Frontend (Razorpay Checkout) 
    ↓ (Payment Success + Signature)
Backend Verification
    ↓ (HMAC SHA256)
Signature Validation
    ↓ (Success/Failure)
Payment Processing
```

## 📋 Implementation Approaches

### Approach 1: Manual HMAC SHA256 Implementation (Current)

This is the approach currently implemented in our project:

```java
@Component
public class RazorpaySignatureVerifier {
    
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;
    
    public boolean verifyPaymentSignature(String paymentId, String orderId, String signature) {
        try {
            // Create the expected signature
            String expectedSignature = generateSignature(paymentId + "|" + orderId);
            
            // Compare signatures
            return expectedSignature.equals(signature);
            
        } catch (Exception e) {
            System.err.println("Error verifying Razorpay signature: " + e.getMessage());
            return false;
        }
    }
    
    public String generateSignature(String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                razorpaySecret.getBytes(), "HmacSHA256");
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
            
        } catch (Exception e) {
            System.err.println("Error generating signature: " + e.getMessage());
            return null;
        }
    }
}
```

### Approach 2: Using Razorpay Java SDK (Recommended)

Add the Razorpay Java SDK dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.razorpay</groupId>
    <artifactId>razorpay-java</artifactId>
    <version>1.4.3</version>
</dependency>
```

Then implement signature verification:

```java
import com.razorpay.Utils;
import org.springframework.stereotype.Component;

@Component
public class RazorpaySignatureVerifierSDK {
    
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;
    
    public boolean verifyPaymentSignature(String paymentId, String orderId, String signature) {
        try {
            // Create the data string in the format expected by Razorpay
            String data = paymentId + "|" + orderId;
            
            // Use Razorpay SDK to verify signature
            return Utils.verifyWebhookSignature(data, signature, razorpaySecret);
            
        } catch (Exception e) {
            System.err.println("Error verifying Razorpay signature: " + e.getMessage());
            return false;
        }
    }
    
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            return Utils.verifyWebhookSignature(payload, signature, razorpaySecret);
        } catch (Exception e) {
            System.err.println("Error verifying webhook signature: " + e.getMessage());
            return false;
        }
    }
}
```

### Approach 3: Enhanced Manual Implementation with Better Error Handling

```java
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EnhancedRazorpaySignatureVerifier {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedRazorpaySignatureVerifier.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;
    
    public boolean verifyPaymentSignature(String paymentId, String orderId, String signature) {
        if (paymentId == null || orderId == null || signature == null) {
            logger.error("Payment ID, Order ID, or Signature is null");
            return false;
        }
        
        try {
            String data = paymentId + "|" + orderId;
            String expectedSignature = generateHmacSha256(data, razorpaySecret);
            
            boolean isValid = expectedSignature.equals(signature);
            
            if (isValid) {
                logger.info("Payment signature verified successfully for payment ID: {}", paymentId);
            } else {
                logger.warn("Payment signature verification failed for payment ID: {}", paymentId);
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("Error verifying payment signature for payment ID: {}", paymentId, e);
            return false;
        }
    }
    
    public boolean verifyWebhookSignature(String payload, String signature) {
        if (payload == null || signature == null) {
            logger.error("Webhook payload or signature is null");
            return false;
        }
        
        try {
            String expectedSignature = generateHmacSha256(payload, razorpaySecret);
            boolean isValid = expectedSignature.equals(signature);
            
            if (isValid) {
                logger.info("Webhook signature verified successfully");
            } else {
                logger.warn("Webhook signature verification failed");
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("Error verifying webhook signature", e);
            return false;
        }
    }
    
    private String generateHmacSha256(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
```

## 🔧 Usage in Payment Service

Here's how to use signature verification in your payment service:

```java
@Service
public class PaymentService {
    
    @Autowired
    private RazorpaySignatureVerifier signatureVerifier;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public PaymentResponse handleSecurePaymentSuccess(RazorpayPaymentVerificationRequest request) {
        try {
            // Step 1: Verify payment signature
            boolean isSignatureValid = signatureVerifier.verifyPaymentSignature(
                request.getRazorpayPaymentId(),
                request.getRazorpayOrderId(),
                request.getRazorpaySignature()
            );
            
            if (!isSignatureValid) {
                logger.error("Invalid payment signature for payment ID: {}", request.getRazorpayPaymentId());
                return new PaymentResponse("Invalid payment signature. Payment verification failed.");
            }
            
            // Step 2: Find existing order in database
            Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(request.getRazorpayOrderId());
            
            if (existingPaymentOpt.isEmpty()) {
                logger.error("Order not found for order ID: {}", request.getRazorpayOrderId());
                return new PaymentResponse("Order not found in our system. Please contact support.");
            }
            
            Payment payment = existingPaymentOpt.get();
            
            // Step 3: Check for duplicate processing
            if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
                logger.warn("Payment already processed for order ID: {}", request.getRazorpayOrderId());
                return new PaymentResponse("Payment has already been processed successfully.");
            }
            
            // Step 4: Update payment with client data
            payment.setPaymentId(request.getRazorpayPaymentId());
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            
            // Update client data if provided
            if (request.getCustomerName() != null && !request.getCustomerName().trim().isEmpty()) {
                payment.setCustomerName(request.getCustomerName());
            }
            if (request.getCustomerEmail() != null && !request.getCustomerEmail().trim().isEmpty()) {
                payment.setCustomerEmail(request.getCustomerEmail());
            }
            if (request.getCustomerPhone() != null && !request.getCustomerPhone().trim().isEmpty()) {
                payment.setCustomerPhone(request.getCustomerPhone());
            }
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                payment.setDescription(request.getDescription());
            }
            
            // Step 5: Save to database
            payment = paymentRepository.save(payment);
            logger.info("Payment processed successfully for payment ID: {}", request.getRazorpayPaymentId());
            
            return new PaymentResponse(payment);
            
        } catch (Exception e) {
            logger.error("Error processing secure payment success for payment ID: {}", 
                request.getRazorpayPaymentId(), e);
            return new PaymentResponse("Failed to process payment. Please try again or contact support.");
        }
    }
}
```

## 🧪 Testing Signature Verification

### Test Cases

```java
@SpringBootTest
class RazorpaySignatureVerifierTest {
    
    @Autowired
    private RazorpaySignatureVerifier signatureVerifier;
    
    @Test
    void testValidPaymentSignature() {
        // Test with known valid data
        String paymentId = "pay_ABC123XYZ";
        String orderId = "order_DEF456UVW";
        String signature = signatureVerifier.generateSignature(paymentId + "|" + orderId);
        
        boolean isValid = signatureVerifier.verifyPaymentSignature(paymentId, orderId, signature);
        assertTrue(isValid, "Valid signature should return true");
    }
    
    @Test
    void testInvalidPaymentSignature() {
        String paymentId = "pay_ABC123XYZ";
        String orderId = "order_DEF456UVW";
        String invalidSignature = "invalid_signature";
        
        boolean isValid = signatureVerifier.verifyPaymentSignature(paymentId, orderId, invalidSignature);
        assertFalse(isValid, "Invalid signature should return false");
    }
    
    @Test
    void testNullParameters() {
        boolean isValid = signatureVerifier.verifyPaymentSignature(null, "order_123", "signature");
        assertFalse(isValid, "Null payment ID should return false");
        
        isValid = signatureVerifier.verifyPaymentSignature("pay_123", null, "signature");
        assertFalse(isValid, "Null order ID should return false");
        
        isValid = signatureVerifier.verifyPaymentSignature("pay_123", "order_123", null);
        assertFalse(isValid, "Null signature should return false");
    }
}
```

### Manual Testing

Create a test endpoint to verify signatures:

```java
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private RazorpaySignatureVerifier signatureVerifier;
    
    @PostMapping("/verify-signature")
    public ResponseEntity<Map<String, Object>> testSignatureVerification(@RequestBody Map<String, String> request) {
        String paymentId = request.get("paymentId");
        String orderId = request.get("orderId");
        String signature = request.get("signature");
        
        boolean isValid = signatureVerifier.verifyPaymentSignature(paymentId, orderId, signature);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("paymentId", paymentId);
        response.put("orderId", orderId);
        response.put("signature", signature);
        response.put("expectedSignature", signatureVerifier.generateSignature(paymentId + "|" + orderId));
        
        return ResponseEntity.ok(response);
    }
}
```

## 🔍 Debugging Signature Verification

### Common Issues and Solutions

1. **Signature Mismatch**
   ```java
   // Debug: Print expected vs actual signature
   String expectedSignature = signatureVerifier.generateSignature(paymentId + "|" + orderId);
   System.out.println("Expected: " + expectedSignature);
   System.out.println("Actual: " + signature);
   ```

2. **Encoding Issues**
   ```java
   // Ensure consistent encoding
   byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
   byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
   ```

3. **Secret Key Issues**
   ```java
   // Verify secret key is loaded correctly
   @PostConstruct
   public void validateConfiguration() {
       if (razorpaySecret == null || razorpaySecret.trim().isEmpty()) {
           throw new IllegalStateException("Razorpay secret key is not configured");
       }
       logger.info("Razorpay secret key loaded successfully");
   }
   ```

## 🛡️ Security Best Practices

### 1. Environment Variables
```properties
# application.properties
razorpay.key.secret=${RAZORPAY_SECRET_KEY}
```

### 2. Secret Management
```java
// Use Spring Cloud Config or AWS Secrets Manager in production
@Value("${razorpay.key.secret}")
private String razorpaySecret;
```

### 3. Logging Security
```java
// Don't log sensitive data
logger.info("Payment signature verification completed for payment ID: {}", paymentId);
// NOT: logger.info("Signature: {}", signature);
```

### 4. Rate Limiting
```java
@RateLimiter(name = "paymentVerification")
public PaymentResponse handleSecurePaymentSuccess(RazorpayPaymentVerificationRequest request) {
    // Implementation
}
```

## 📊 Monitoring and Alerting

### Health Check Endpoint
```java
@RestController
public class HealthController {
    
    @Autowired
    private RazorpaySignatureVerifier signatureVerifier;
    
    @GetMapping("/health/signature-verifier")
    public ResponseEntity<Map<String, Object>> signatureVerifierHealth() {
        try {
            // Test signature verification
            String testSignature = signatureVerifier.generateSignature("test|data");
            boolean isValid = signatureVerifier.verifyPaymentSignature("test", "data", testSignature);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", isValid ? "UP" : "DOWN");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
```

## 🔗 Integration with Frontend

### Frontend Signature Verification (Optional)
```javascript
// Client-side signature verification (for additional security)
async function verifySignature(paymentId, orderId, signature) {
    const response = await fetch('/api/test/verify-signature', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            paymentId: paymentId,
            orderId: orderId,
            signature: signature
        })
    });
    
    const result = await response.json();
    return result.valid;
}
```

## 📝 Summary

Signature verification is a critical security measure for Razorpay integration:

1. **Always verify signatures** before processing payments
2. **Use the Razorpay SDK** when possible for better reliability
3. **Implement proper error handling** and logging
4. **Test thoroughly** with various scenarios
5. **Monitor signature verification** in production
6. **Keep secret keys secure** using environment variables

The current implementation in your project provides a solid foundation for signature verification, but consider upgrading to the Razorpay SDK for production use.

## 🔗 Related Documentation

- [Razorpay Signature Verification](https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/signature-generation/)
- [Spring Boot Security](https://spring.io/projects/spring-security)
- [HMAC SHA256 Implementation](https://docs.oracle.com/javase/8/docs/api/javax/crypto/Mac.html) 