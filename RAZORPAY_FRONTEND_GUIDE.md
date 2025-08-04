# Razorpay Frontend Integration Guide

This guide explains how to test the complete Razorpay payment integration with the Spring Boot backend.

## 🚀 Quick Start

### 1. Start the Spring Boot Application
```bash
cd PaymentGateway
./mvnw spring-boot:run
```

### 2. Access the Payment Pages

- **Main Payment Page**: http://localhost:8080/
- **Dedicated Test Page**: http://localhost:8080/razorpay-test.html

## 📋 Test Flow

### Step 1: Fill the Payment Form
- **Customer Name**: John Doe (pre-filled)
- **Email**: john.doe@example.com (pre-filled)
- **Phone**: +91-9876543210 (pre-filled)
- **Amount**: 100 (in rupees)
- **Description**: Test payment for demo (pre-filled)

### Step 2: Click "Pay with Razorpay"
- The form will call your backend at `/api/payments/razorpay/order`
- Backend creates a Razorpay order and returns the `orderId`
- Frontend opens Razorpay Checkout modal

### Step 3: Complete Payment in Razorpay Modal
Use these test credentials:
- **Card Number**: 4111 1111 1111 1111
- **Expiry Date**: Any future date (e.g., 12/25)
- **CVV**: Any 3 digits (e.g., 123)
- **OTP**: Any 6 digits (e.g., 123456)

### Step 4: View Payment Result
After successful payment, you'll see:
- Payment ID
- Order ID
- Signature
- Amount and customer details

## 🔧 Technical Details

### Backend Integration
- **Endpoint**: `POST /api/payments/razorpay/order`
- **Request**: `{ "amount": 10000, "currency": "INR", "receipt": "receipt_123" }`
- **Response**: `{ "orderId": "order_ABC123", "amount": 10000, ... }`

### Frontend Integration
- **Razorpay SDK**: Loaded from `https://checkout.razorpay.com/v1/checkout.js`
- **Test Key**: `rzp_test_51KtV2qBmMIfJm`
- **Amount Conversion**: Rupees to paise (× 100)

### Key Features
- ✅ Real-time order creation via backend
- ✅ Razorpay Checkout modal integration
- ✅ Test card support
- ✅ Error handling
- ✅ Loading states
- ✅ Responsive design

## 🧪 Testing Scenarios

### 1. Successful Payment
- Use test card: 4111 1111 1111 1111
- Any future expiry date
- Any CVV and OTP

### 2. Failed Payment
- Use test card: 4000 0000 0000 0002
- This will simulate a failed payment

### 3. Network Error
- Disconnect internet before clicking "Pay with Razorpay"
- Should show error message

### 4. Invalid Amount
- Enter 0 or negative amount
- Should show validation error

## 📱 Mobile Testing

The payment pages are fully responsive and work on:
- ✅ Desktop browsers
- ✅ Mobile browsers
- ✅ Tablets

## 🔍 Debugging

### Browser Console
Open Developer Tools (F12) to see:
- Order creation requests
- Razorpay checkout initialization
- Payment success/failure callbacks

### Network Tab
Monitor:
- `POST /api/payments/razorpay/order` - Backend order creation
- Razorpay API calls - Payment processing

## 🛠️ Customization

### Change Test Amount
Edit the form or modify the auto-fill values in the JavaScript:
```javascript
document.getElementById('amount').value = '500'; // ₹500
```

### Change Razorpay Key
Update the key in both HTML files:
```javascript
const RAZORPAY_KEY_ID = 'your_new_test_key';
```

### Modify Styling
The pages use CSS Grid and Flexbox for responsive design. Colors and styling can be customized in the `<style>` section.

## 🚨 Important Notes

1. **Test Environment**: Uses Razorpay test keys - no real money is charged
2. **Amount in Paise**: Backend expects amount in paise (smallest currency unit)
3. **CORS**: Backend is configured to allow all origins for testing
4. **Security**: For production, implement proper authentication and validation

## 📞 Support

If you encounter issues:
1. Check browser console for errors
2. Verify backend is running on port 8080
3. Ensure internet connection for Razorpay SDK
4. Check that test keys are correctly configured

## 🔗 Useful Links

- [Razorpay Documentation](https://razorpay.com/docs/)
- [Razorpay Test Cards](https://razorpay.com/docs/payments/payments/test-mode/test-cards/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot) 