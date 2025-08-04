# Razorpay Order Creation API

This document describes the new POST endpoint for creating Razorpay orders using the Razorpay Java SDK.

## Endpoint

**POST** `/api/payments/razorpay/order`

## Description

Creates a new Razorpay order using the Razorpay Java SDK with test keys. The endpoint accepts payment details and returns the generated order ID along with other order information.

## Request Body

```json
{
  "amount": 1000,
  "currency": "INR",
  "receipt": "receipt_123"
}
```

### Parameters

- **amount** (required): Amount in paise (1 INR = 100 paise)
  - Type: Integer
  - Must be greater than 0
  - Example: 1000 for ₹10, 5000 for ₹50

- **currency** (optional): Currency code
  - Type: String
  - Default: "INR"
  - Example: "INR", "USD"

- **receipt** (optional): Receipt identifier
  - Type: String
  - Default: Auto-generated with timestamp
  - Example: "receipt_123", "order_456"

## Response

### Success Response (200 OK)

```json
{
  "orderId": "order_ABC123XYZ",
  "amount": 1000,
  "currency": "INR",
  "receipt": "receipt_123",
  "status": "created",
  "createdAt": 1640995200
}
```

### Error Response (400 Bad Request)

```json
{
  "error": "Invalid amount. Amount must be greater than 0 and in paise."
}
```

### Error Response (500 Internal Server Error)

```json
{
  "error": "Failed to create Razorpay order: [error message]",
  "errorCode": "BAD_REQUEST_ERROR"
}
```

## Configuration

The endpoint uses the following configuration from `application.properties`:

```properties
razorpay.key.id=rzp_test_51KtV2qBmMIfJm
razorpay.key.secret=thisisatestsecretkey
```

## Testing

### Using curl

```bash
# Create an order for ₹10 (1000 paise)
curl -X POST http://localhost:8080/api/payments/razorpay/order \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000,
    "currency": "INR",
    "receipt": "test_receipt_001"
  }'
```

### Using the provided test scripts

- **Linux/Mac**: Run `./test-razorpay-order.sh`
- **Windows**: Run `test-razorpay-order.bat`

## Important Notes

1. **Amount in Paise**: Razorpay expects amounts in paise (smallest currency unit). For INR, 1 rupee = 100 paise.

2. **Test Keys**: The current configuration uses test keys. For production, replace with actual Razorpay keys.

3. **Error Handling**: The endpoint includes comprehensive error handling for:
   - Invalid amounts
   - Razorpay API errors
   - Unexpected exceptions

4. **Dependencies**: The endpoint requires:
   - Razorpay Java SDK (already included in pom.xml)
   - JSON library (added to pom.xml)

## Integration with Frontend

The returned `orderId` can be used with Razorpay's frontend SDK to initiate the payment flow:

```javascript
var options = {
  key: "rzp_test_51KtV2qBmMIfJm",
  amount: 1000,
  currency: "INR",
  order_id: "order_ABC123XYZ", // Use the orderId from this endpoint
  // ... other options
};
```

## Security Considerations

- Always use HTTPS in production
- Validate input data on both frontend and backend
- Store sensitive keys securely (use environment variables)
- Implement proper authentication and authorization
- Monitor API usage and implement rate limiting 