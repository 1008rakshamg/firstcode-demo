@echo off
REM Razorpay Signature Verification Test Script for Windows
REM This script demonstrates how to test signature verification endpoints

set BASE_URL=http://localhost:8080
set API_BASE=%BASE_URL%/api/test

echo 🔐 Razorpay Signature Verification Test Script
echo ==============================================
echo.

echo 1. Testing Signature Verifier Health Check
echo ------------------------------------------
curl -s -X GET "%API_BASE%/signature-verifier-health" -H "Content-Type: application/json"
echo.
echo.

echo 2. Getting Signature Verification Information
echo ---------------------------------------------
curl -s -X GET "%API_BASE%/signature-info" -H "Content-Type: application/json"
echo.
echo.

echo 3. Generating Test Signature
echo ----------------------------
curl -s -X POST "%API_BASE%/generate-signature" -H "Content-Type: application/json" -d "{\"paymentId\":\"pay_test123456\",\"orderId\":\"order_test789012\"}"
echo.
echo.

echo 4. Verifying Valid Signature
echo ----------------------------
echo Note: This test requires the signature from the previous step
echo You can manually test with:
echo curl -X POST "%API_BASE%/verify-signature" -H "Content-Type: application/json" -d "{\"paymentId\":\"pay_test123456\",\"orderId\":\"order_test789012\",\"signature\":\"your_signature_here\"}"
echo.

echo 5. Verifying Invalid Signature
echo ------------------------------
curl -s -X POST "%API_BASE%/verify-signature" -H "Content-Type: application/json" -d "{\"paymentId\":\"pay_test123456\",\"orderId\":\"order_test789012\",\"signature\":\"invalid_signature_12345\"}"
echo.
echo.

echo 6. Testing with Missing Parameters
echo ----------------------------------
curl -s -X POST "%API_BASE%/verify-signature" -H "Content-Type: application/json" -d "{\"paymentId\":\"pay_test123456\"}"
echo.
echo.

echo 7. Real-world Payment Signature Example
echo ---------------------------------------
curl -s -X POST "%API_BASE%/generate-signature" -H "Content-Type: application/json" -d "{\"paymentId\":\"pay_ABC123XYZ\",\"orderId\":\"order_DEF456UVW\"}"
echo.
echo.

echo 📊 Test Summary
echo ===============
echo ✅ All signature verification tests completed
echo ℹ️  Check the responses above for verification results
echo.
echo ℹ️  To test manually, you can use:
echo   curl -X POST %API_BASE%/verify-signature ^
echo     -H "Content-Type: application/json" ^
echo     -d "{\"paymentId\":\"pay_test123\",\"orderId\":\"order_test456\",\"signature\":\"your_signature\"}"
echo.
echo ℹ️  For more information, visit:
echo   %API_BASE%/signature-info
echo   %API_BASE%/signature-verifier-health
echo.
pause 