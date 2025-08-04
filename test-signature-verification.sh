#!/bin/bash

# Razorpay Signature Verification Test Script
# This script demonstrates how to test signature verification endpoints

BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api/test"

echo "🔐 Razorpay Signature Verification Test Script"
echo "=============================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    if [ "$status" = "SUCCESS" ]; then
        echo -e "${GREEN}✅ $message${NC}"
    elif [ "$status" = "ERROR" ]; then
        echo -e "${RED}❌ $message${NC}"
    elif [ "$status" = "INFO" ]; then
        echo -e "${BLUE}ℹ️  $message${NC}"
    elif [ "$status" = "WARNING" ]; then
        echo -e "${YELLOW}⚠️  $message${NC}"
    fi
}

# Function to make HTTP requests
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    
    if [ "$method" = "GET" ]; then
        curl -s -X GET "$endpoint" -H "Content-Type: application/json"
    else
        curl -s -X POST "$endpoint" -H "Content-Type: application/json" -d "$data"
    fi
}

# Test 1: Health Check
echo "1. Testing Signature Verifier Health Check"
echo "------------------------------------------"
response=$(make_request "GET" "$API_BASE/signature-verifier-health")
echo "Response: $response"
echo ""

# Test 2: Get Signature Info
echo "2. Getting Signature Verification Information"
echo "---------------------------------------------"
response=$(make_request "GET" "$API_BASE/signature-info")
echo "Response: $response"
echo ""

# Test 3: Generate Signature
echo "3. Generating Test Signature"
echo "----------------------------"
test_data='{
    "paymentId": "pay_test123456",
    "orderId": "order_test789012"
}'
response=$(make_request "POST" "$API_BASE/generate-signature" "$test_data")
echo "Request: $test_data"
echo "Response: $response"
echo ""

# Extract signature from response for next test
signature=$(echo $response | grep -o '"signature":"[^"]*"' | cut -d'"' -f4)
if [ -n "$signature" ]; then
    print_status "SUCCESS" "Signature generated successfully: $signature"
else
    print_status "ERROR" "Failed to extract signature from response"
fi
echo ""

# Test 4: Verify Valid Signature
echo "4. Verifying Valid Signature"
echo "----------------------------"
verify_data='{
    "paymentId": "pay_test123456",
    "orderId": "order_test789012",
    "signature": "'$signature'"
}'
response=$(make_request "POST" "$API_BASE/verify-signature" "$verify_data")
echo "Request: $verify_data"
echo "Response: $response"
echo ""

# Test 5: Verify Invalid Signature
echo "5. Verifying Invalid Signature"
echo "------------------------------"
invalid_verify_data='{
    "paymentId": "pay_test123456",
    "orderId": "order_test789012",
    "signature": "invalid_signature_12345"
}'
response=$(make_request "POST" "$API_BASE/verify-signature" "$invalid_verify_data")
echo "Request: $invalid_verify_data"
echo "Response: $response"
echo ""

# Test 6: Test with Missing Parameters
echo "6. Testing with Missing Parameters"
echo "----------------------------------"
missing_data='{
    "paymentId": "pay_test123456"
}'
response=$(make_request "POST" "$API_BASE/verify-signature" "$missing_data")
echo "Request: $missing_data"
echo "Response: $response"
echo ""

# Test 7: Webhook Signature Verification
echo "7. Testing Webhook Signature Verification"
echo "----------------------------------------"
webhook_payload='{"event":"payment.captured","payload":{"payment":{"entity":{"id":"pay_test123456","amount":1000,"currency":"INR"}}}}'
webhook_signature=$(echo -n "$webhook_payload" | openssl dgst -sha256 -hmac "thisisatestsecretkey" | cut -d' ' -f2)

webhook_data='{
    "payload": "'$webhook_payload'",
    "signature": "'$webhook_signature'"
}'
response=$(make_request "POST" "$API_BASE/verify-webhook-signature" "$webhook_data")
echo "Request: $webhook_data"
echo "Response: $response"
echo ""

# Test 8: Real-world Example
echo "8. Real-world Payment Signature Example"
echo "---------------------------------------"
real_payment_data='{
    "paymentId": "pay_ABC123XYZ",
    "orderId": "order_DEF456UVW"
}'
response=$(make_request "POST" "$API_BASE/generate-signature" "$real_payment_data")
echo "Request: $real_payment_data"
echo "Response: $response"
echo ""

# Extract real signature
real_signature=$(echo $response | grep -o '"signature":"[^"]*"' | cut -d'"' -f4)
if [ -n "$real_signature" ]; then
    real_verify_data='{
        "paymentId": "pay_ABC123XYZ",
        "orderId": "order_DEF456UVW",
        "signature": "'$real_signature'"
    }'
    response=$(make_request "POST" "$API_BASE/verify-signature" "$real_verify_data")
    echo "Verification Request: $real_verify_data"
    echo "Verification Response: $response"
else
    print_status "ERROR" "Failed to extract real signature"
fi
echo ""

# Summary
echo "📊 Test Summary"
echo "==============="
print_status "INFO" "All signature verification tests completed"
print_status "INFO" "Check the responses above for verification results"
echo ""
print_status "INFO" "To test manually, you can use:"
echo "  curl -X POST $API_BASE/verify-signature \\"
echo "    -H 'Content-Type: application/json' \\"
echo "    -d '{\"paymentId\":\"pay_test123\",\"orderId\":\"order_test456\",\"signature\":\"your_signature\"}'"
echo ""
print_status "INFO" "For more information, visit:"
echo "  $API_BASE/signature-info"
echo "  $API_BASE/signature-verifier-health" 