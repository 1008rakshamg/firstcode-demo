# Payment Gateway Prototype

A comprehensive payment gateway prototype built with Spring Boot, similar to Razorpay. This project demonstrates a complete payment processing system with merchant management, payment processing, webhook handling, and a modern web interface.

## 🚀 Features

### Core Payment Features
- **Payment Processing**: Create, verify, and manage payments
- **Multiple Payment Methods**: Support for cards, UPI, net banking, and wallets
- **Payment Status Tracking**: Real-time status updates (PENDING, AUTHORIZED, CAPTURED, SUCCESS, FAILED, etc.)
- **Refund Management**: Full and partial refund capabilities
- **Webhook Integration**: Real-time payment notifications
- **Signature Verification**: Secure payment verification

### Merchant Management
- **Merchant Registration**: Complete merchant onboarding
- **API Key Management**: Secure API access
- **Merchant Dashboard**: Payment analytics and reporting
- **Multi-currency Support**: INR, USD, EUR, and more

### Security & Compliance
- **JWT Authentication**: Secure API access
- **Input Validation**: Comprehensive request validation
- **Error Handling**: Global exception handling
- **Audit Logging**: Complete payment audit trail
- **PCI Compliance**: Secure payment data handling

### Technical Features
- **RESTful APIs**: Complete REST API suite
- **Database Integration**: JPA with H2/PostgreSQL
- **Caching**: Redis-based caching
- **Monitoring**: Actuator endpoints for health checks
- **API Documentation**: Swagger/OpenAPI documentation
- **Docker Support**: Containerized deployment

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.4, Java 17
- **Database**: H2 (Development), PostgreSQL (Production)
- **Cache**: Redis
- **Payment Gateway**: Razorpay Integration
- **Security**: Spring Security, JWT
- **Documentation**: Swagger/OpenAPI
- **Frontend**: HTML5, CSS3, JavaScript
- **Containerization**: Docker, Docker Compose

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional)
- Redis (optional, for caching)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd PaymentGateway
```

### 2. Configure Application
Edit `src/main/resources/application.properties`:
```properties
# Razorpay Configuration (Get from Razorpay Dashboard)
razorpay.key.id=your_razorpay_key_id
razorpay.key.secret=your_razorpay_secret_key
razorpay.webhook.secret=your_webhook_secret

# JWT Secret
jwt.secret=your_very_long_and_secure_jwt_secret_key
```

### 3. Run the Application

#### Option A: Using Maven
```bash
mvn spring-boot:run
```

#### Option B: Using Docker
```bash
docker-compose up -d
```

#### Option C: Using Scripts
```bash
# Windows
run.bat

# Linux/Mac
./run.sh
```

### 4. Access the Application
- **Web Interface**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health

## 📚 API Documentation

### Payment Endpoints

#### Create Payment
```http
POST /api/payments/create
Content-Type: application/json

{
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "+919876543210",
  "amount": 1000.00,
  "currency": "INR",
  "description": "Payment for order #12345",
  "notes": "Additional notes"
}
```

#### Verify Payment
```http
POST /api/payments/verify
Content-Type: application/x-www-form-urlencoded

paymentId=pay_1234567890&orderId=ORDER_1234567890&signature=abc123
```

#### Get Payment Details
```http
GET /api/payments/{id}
GET /api/payments/order/{orderId}
```

#### Get Payments by Customer
```http
GET /api/payments/customer/{email}
```

#### Get Payments by Status
```http
GET /api/payments/status/{status}
```

#### Refund Payment
```http
POST /api/payments/{id}/refund
```

### Merchant Endpoints

#### Register Merchant
```http
POST /api/merchants/register
Content-Type: application/json

{
  "businessName": "My Store",
  "contactName": "John Doe",
  "email": "john@mystore.com",
  "phone": "+919876543210",
  "address": "123 Main St",
  "city": "Mumbai",
  "state": "Maharashtra",
  "postalCode": "400001",
  "country": "India"
}
```

#### Get Merchant Details
```http
GET /api/merchants/{id}
GET /api/merchants/merchant-id/{merchantId}
GET /api/merchants/email/{email}
```

### Webhook Endpoints

#### Payment Webhook
```http
POST /api/webhooks/payment
Content-Type: application/json

{
  "event": "payment.captured",
  "payload": {
    "payment": {
      "id": "pay_1234567890",
      "amount": 100000,
      "currency": "INR",
      "status": "captured"
    }
  }
}
```

## 🗄️ Database Schema

### Payments Table
- `id`: Primary key
- `orderId`: Unique order identifier
- `paymentId`: Payment gateway payment ID
- `customerName`: Customer's full name
- `customerEmail`: Customer's email
- `customerPhone`: Customer's phone number
- `amount`: Payment amount
- `currency`: Payment currency
- `status`: Payment status
- `createdAt`: Payment creation timestamp
- `updatedAt`: Last update timestamp
- `description`: Payment description
- `notes`: Additional notes
- `gatewayResponse`: Gateway response data
- `transactionId`: Transaction ID
- `paymentMethod`: Payment method used
- `merchantId`: Associated merchant ID

### Merchants Table
- `id`: Primary key
- `merchantId`: Unique merchant identifier
- `businessName`: Business name
- `contactName`: Contact person name
- `email`: Business email
- `phone`: Business phone
- `address`: Business address
- `city`: City
- `state`: State/Province
- `postalCode`: Postal code
- `country`: Country
- `status`: Merchant status
- `apiKey`: API key for integration
- `secretKey`: Secret key for integration
- `webhookUrl`: Webhook URL
- `defaultCurrency`: Default currency
- `isLiveMode`: Live mode flag

## 🔧 Configuration

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/paymentdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Razorpay
RAZORPAY_KEY_ID=your_key_id
RAZORPAY_KEY_SECRET=your_secret_key
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# JWT
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
```

### Application Properties
Key configuration options in `application.properties`:
- Database connection settings
- Razorpay integration settings
- Security configuration
- Logging levels
- Cache settings
- Rate limiting
- Payment limits

## 🧪 Testing

### API Testing
Use the provided `test-api.sh` script:
```bash
./test-api.sh
```

### Manual Testing
1. Access the web interface at http://localhost:8080
2. Fill in payment details
3. Submit payment request
4. Verify payment status

### Unit Testing
```bash
mvn test
```

## 📊 Monitoring & Health Checks

### Actuator Endpoints
- `/actuator/health`: Application health
- `/actuator/info`: Application information
- `/actuator/metrics`: Application metrics
- `/actuator/prometheus`: Prometheus metrics

### Health Check Response
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 419430400000,
        "threshold": 10485760
      }
    }
  }
}
```

## 🔒 Security Considerations

### API Security
- JWT-based authentication
- Request validation
- Rate limiting
- CORS configuration
- Input sanitization

### Payment Security
- Signature verification
- Secure key management
- PCI compliance
- Audit logging
- Data encryption

### Best Practices
- Use HTTPS in production
- Rotate API keys regularly
- Monitor for suspicious activity
- Implement proper error handling
- Regular security audits

## 🚀 Deployment

### Docker Deployment
```bash
# Build image
docker build -t payment-gateway .

# Run container
docker run -p 8080:8080 payment-gateway
```

### Docker Compose
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Deployment
1. Configure production database (PostgreSQL)
2. Set up Redis for caching
3. Configure SSL/TLS certificates
4. Set up monitoring and logging
5. Configure backup strategies
6. Set up CI/CD pipeline

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation
- Review the logs for debugging

## 🔄 Roadmap

- [ ] Multi-currency support
- [ ] Advanced analytics dashboard
- [ ] Mobile SDK
- [ ] Subscription management
- [ ] International payment methods
- [ ] Advanced fraud detection
- [ ] Multi-tenant architecture
- [ ] Real-time notifications
- [ ] Advanced reporting
- [ ] API rate limiting
- [ ] Webhook retry mechanism
- [ ] Payment link generation
- [ ] QR code payments
- [ ] Split payments
- [ ] Payment disputes handling

---

**Note**: This is a prototype/demo application. For production use, ensure proper security measures, compliance requirements, and thorough testing. 