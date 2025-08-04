@echo off
echo Testing Razorpay Order Creation Endpoint...
echo ==========================================

echo.
echo Test 1: Creating order with 1000 paise (10 INR)
curl -X POST http://localhost:8080/api/payments/razorpay/order -H "Content-Type: application/json" -d "{\"amount\": 1000, \"currency\": \"INR\", \"receipt\": \"test_receipt_001\"}"

echo.
echo.
echo Test 2: Creating order with 5000 paise (50 INR)
curl -X POST http://localhost:8080/api/payments/razorpay/order -H "Content-Type: application/json" -d "{\"amount\": 5000, \"currency\": \"INR\"}"

echo.
echo.
echo Test 3: Testing with invalid amount
curl -X POST http://localhost:8080/api/payments/razorpay/order -H "Content-Type: application/json" -d "{\"amount\": 0, \"currency\": \"INR\"}"

echo.
pause 