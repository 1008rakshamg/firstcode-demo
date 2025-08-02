# Razorpay Payment Gateway - Spring Boot

A complete payment gateway integration using **Spring Boot** and **Razorpay** with a modern web interface. This prototype demonstrates secure payment processing, order creation, payment verification, and transaction management.

## 🚀 Features

- **Complete Payment Flow**: Order creation, payment processing, and verification
- **Razorpay Integration**: Official Razorpay Java SDK integration
- **Modern Web UI**: Responsive Bootstrap-based frontend with animations
- **Payment Verification**: Secure signature verification using HMAC-SHA256
- **Database Persistence**: JPA/Hibernate with H2 database
- **RESTful APIs**: Complete REST API for payment operations
- **Error Handling**: Comprehensive error handling and validation
- **Security**: HTTPS-ready with secure payment processing
- **Logging**: Detailed logging for debugging and monitoring

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 (in-memory), JPA/Hibernate
- **Payment Gateway**: Razorpay Java SDK 1.4.3
- **Frontend**: Bootstrap 5, HTML5, JavaScript
- **Build Tool**: Maven
- **Template Engine**: Thymeleaf

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Razorpay account (for API keys)
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## ⚙️ Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd razorpay-payment-gateway
```

### 2. Configure Razorpay API Keys

#### Option A: Environment Variables (Recommended)
```bash
export RAZORPAY_KEY_ID=your_razorpay_key_id
export RAZORPAY_KEY_SECRET=your_razorpay_key_secret
export RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

#### Option B: Update application.yml
Edit `src/main/resources/application.yml`:
```yaml
razorpay:
  key:
    id: rzp_test_your_actual_key_id
    secret: your_actual_key_secret
  webhook:
    secret: your_actual_webhook_secret
```

### 3. Build and Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔧 Getting Razorpay API Keys

1. Sign up at [Razorpay Dashboard](https://dashboard.razorpay.com/)
2. Navigate to **Settings** → **API Keys**
3. Generate **Test Keys** for development
4. Copy the **Key ID** and **Key Secret**
5. For webhooks, generate a **Webhook Secret**

## 📱 Usage

### Web Interface

1. Open `http://localhost:8080` in your browser
2. Fill in the payment form:
   - **Amount**: Enter payment amount
   - **Customer Details**: Name, email, phone
   - **Notes**: Optional additional information
3. Click **Pay Securely** to initiate payment
4. Complete payment in Razorpay checkout modal
5. View success/failure page based on payment status

### API Endpoints

#### Create Payment Order
```http
POST /api/payments/create-order
Content-Type: application/json

{
  "amount": 100.00,
  "currency": "INR",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9876543210",
  "notes": "Test payment"
}
```

#### Verify Payment
```http
POST /api/payments/verify
Content-Type: application/json

{
  "razorpayOrderId": "order_xyz",
  "razorpayPaymentId": "pay_abc",
  "razorpaySignature": "signature_hash"
}
```

#### Get All Orders
```http
GET /api/payments/orders
```

#### Get Order by ID
```http
GET /api/payments/orders/{id}
```

#### Health Check
```http
GET /api/payments/health
```

## 🗃 Database Schema

The application uses H2 in-memory database with the following schema:

### PaymentOrder Table
```sql
CREATE TABLE payment_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    razorpay_order_id VARCHAR(255) UNIQUE,
    razorpay_payment_id VARCHAR(255),
    razorpay_signature VARCHAR(255),
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    receipt VARCHAR(255),
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    notes TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Payment Status Enum
- `CREATED`: Order created, payment not attempted
- `ATTEMPTED`: Payment attempted but not completed
- `PAID`: Payment successful and verified
- `FAILED`: Payment failed or verification failed
- `CANCELLED`: Payment cancelled by user
- `REFUNDED`: Payment refunded

## 🔒 Security Features

1. **Signature Verification**: All payments are verified using HMAC-SHA256
2. **Input Validation**: Comprehensive validation using Bean Validation
3. **CORS Configuration**: Configurable CORS for frontend integration
4. **Environment Variables**: Sensitive data stored in environment variables
5. **HTTPS Ready**: Application configured for HTTPS deployment

## 🧪 Testing

### Manual Testing
1. Start the application
2. Navigate to `http://localhost:8080`
3. Use test card details provided by Razorpay
4. Complete the payment flow

### API Testing with cURL

```bash
# Create order
curl -X POST http://localhost:8080/api/payments/create-order \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10.00,
    "currency": "INR",
    "customerName": "Test User",
    "customerEmail": "test@example.com",
    "customerPhone": "9876543210"
  }'

# Health check
curl http://localhost:8080/api/payments/health
```

## 📊 Monitoring

### H2 Database Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

### Application Logs
The application provides detailed logging for:
- Payment order creation
- Payment verification
- Error handling
- Database operations

## 🚀 Deployment

### Production Checklist

1. **Environment Variables**: Set production Razorpay keys
2. **Database**: Replace H2 with production database (MySQL/PostgreSQL)
3. **HTTPS**: Configure SSL/TLS certificates
4. **CORS**: Update CORS configuration for production domains
5. **Logging**: Configure proper logging levels
6. **Monitoring**: Set up application monitoring

### Docker Deployment (Optional)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/razorpay-payment-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues and questions:
- Create an issue in the repository
- Check Razorpay documentation: [https://razorpay.com/docs/](https://razorpay.com/docs/)
- Contact: support@example.com

## 🔗 Useful Links

- [Razorpay Documentation](https://razorpay.com/docs/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Bootstrap Documentation](https://getbootstrap.com/docs/)

---

**Note**: This is a prototype for demonstration purposes. For production use, implement additional security measures, comprehensive testing, and proper error handling.
