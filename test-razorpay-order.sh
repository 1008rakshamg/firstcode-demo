#!/bin/bash

# Test script for Razorpay Order Creation Endpoint

echo "Testing Razorpay Order Creation Endpoint..."
echo "=========================================="

# Test data - amount in paise (1000 paise = 10 INR)
curl -X POST http://localhost:8080/api/payments/razorpay/order \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000,
    "currency": "INR",
    "receipt": "test_receipt_001"
  }'

echo ""
echo ""
echo "Test with different amount (5000 paise = 50 INR):"
curl -X POST http://localhost:8080/api/payments/razorpay/order \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000,
    "currency": "INR"
  }'

echo ""
echo ""
echo "Test with invalid amount:"
curl -X POST http://localhost:8080/api/payments/razorpay/order \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 0,
    "currency": "INR"
  }' 