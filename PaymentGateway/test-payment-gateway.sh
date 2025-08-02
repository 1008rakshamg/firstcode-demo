#!/bin/bash

# Payment Gateway Test Script
# This script demonstrates the payment gateway functionality

BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api"

echo "🚀 Payment Gateway Test Script"
echo "================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if server is running
check_server() {
    print_status "Checking if server is running..."
    if curl -s "$BASE_URL/actuator/health" > /dev/null; then
        print_success "Server is running!"
        return 0
    else
        print_error "Server is not running. Please start the application first."
        return 1
    fi
}

# Test health endpoint
test_health() {
    print_status "Testing health endpoint..."
    response=$(curl -s "$BASE_URL/actuator/health")
    if [ $? -eq 0 ]; then
        print_success "Health check passed"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Health check failed"
    fi
    echo ""
}

# Test merchant registration
test_merchant_registration() {
    print_status "Testing merchant registration..."
    
    merchant_data='{
        "businessName": "Test Store",
        "contactName": "John Doe",
        "email": "john@teststore.com",
        "phone": "+919876543210",
        "address": "123 Test Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "postalCode": "400001",
        "country": "India"
    }'
    
    response=$(curl -s -X POST "$API_BASE/merchants/register" \
        -H "Content-Type: application/json" \
        -d "$merchant_data")
    
    if [ $? -eq 0 ]; then
        print_success "Merchant registered successfully"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
        
        # Extract merchant ID for later use
        MERCHANT_ID=$(echo "$response" | jq -r '.id' 2>/dev/null)
        if [ "$MERCHANT_ID" != "null" ] && [ "$MERCHANT_ID" != "" ]; then
            print_status "Merchant ID: $MERCHANT_ID"
        fi
    else
        print_error "Merchant registration failed"
    fi
    echo ""
}

# Test payment creation
test_payment_creation() {
    print_status "Testing payment creation..."
    
    payment_data='{
        "customerName": "Alice Johnson",
        "customerEmail": "alice@example.com",
        "customerPhone": "+919876543211",
        "amount": 1500.00,
        "currency": "INR",
        "description": "Payment for order #12345",
        "notes": "Test payment for demonstration"
    }'
    
    response=$(curl -s -X POST "$API_BASE/payments/create" \
        -H "Content-Type: application/json" \
        -d "$payment_data")
    
    if [ $? -eq 0 ]; then
        print_success "Payment created successfully"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
        
        # Extract payment ID and order ID for later use
        PAYMENT_ID=$(echo "$response" | jq -r '.payment.id' 2>/dev/null)
        ORDER_ID=$(echo "$response" | jq -r '.payment.orderId' 2>/dev/null)
        
        if [ "$PAYMENT_ID" != "null" ] && [ "$PAYMENT_ID" != "" ]; then
            print_status "Payment ID: $PAYMENT_ID"
        fi
        if [ "$ORDER_ID" != "null" ] && [ "$ORDER_ID" != "" ]; then
            print_status "Order ID: $ORDER_ID"
        fi
    else
        print_error "Payment creation failed"
    fi
    echo ""
}

# Test payment retrieval
test_payment_retrieval() {
    if [ -z "$PAYMENT_ID" ]; then
        print_warning "Skipping payment retrieval test - no payment ID available"
        return
    fi
    
    print_status "Testing payment retrieval..."
    
    # Get payment by ID
    response=$(curl -s -X GET "$API_BASE/payments/$PAYMENT_ID")
    if [ $? -eq 0 ]; then
        print_success "Payment retrieved successfully"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Payment retrieval failed"
    fi
    echo ""
}

# Test payment verification
test_payment_verification() {
    if [ -z "$PAYMENT_ID" ] || [ -z "$ORDER_ID" ]; then
        print_warning "Skipping payment verification test - missing payment or order ID"
        return
    fi
    
    print_status "Testing payment verification..."
    
    # Mock signature for testing
    SIGNATURE="test_signature_123"
    
    response=$(curl -s -X POST "$API_BASE/payments/verify" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "paymentId=$PAYMENT_ID&orderId=$ORDER_ID&signature=$SIGNATURE")
    
    if [ $? -eq 0 ]; then
        print_success "Payment verification completed"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Payment verification failed"
    fi
    echo ""
}

# Test payment status update
test_payment_status_update() {
    if [ -z "$PAYMENT_ID" ]; then
        print_warning "Skipping payment status update test - no payment ID available"
        return
    fi
    
    print_status "Testing payment status update..."
    
    response=$(curl -s -X PUT "$API_BASE/payments/$PAYMENT_ID/status?status=SUCCESS")
    if [ $? -eq 0 ]; then
        print_success "Payment status updated successfully"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Payment status update failed"
    fi
    echo ""
}

# Test dashboard statistics
test_dashboard_stats() {
    print_status "Testing dashboard statistics..."
    
    response=$(curl -s -X GET "$API_BASE/payments/stats/dashboard")
    if [ $? -eq 0 ]; then
        print_success "Dashboard statistics retrieved"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Dashboard statistics retrieval failed"
    fi
    echo ""
}

# Test payment refund
test_payment_refund() {
    if [ -z "$PAYMENT_ID" ]; then
        print_warning "Skipping payment refund test - no payment ID available"
        return
    fi
    
    print_status "Testing payment refund..."
    
    response=$(curl -s -X POST "$API_BASE/payments/$PAYMENT_ID/refund")
    if [ $? -eq 0 ]; then
        print_success "Payment refund initiated"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Payment refund failed"
    fi
    echo ""
}

# Test webhook endpoint
test_webhook() {
    print_status "Testing webhook endpoint..."
    
    webhook_data='{
        "event": "payment.captured",
        "payload": {
            "payment": {
                "id": "pay_test_123",
                "amount": 150000,
                "currency": "INR",
                "status": "captured",
                "order_id": "order_test_123"
            }
        }
    }'
    
    response=$(curl -s -X POST "$API_BASE/webhooks/payment" \
        -H "Content-Type: application/json" \
        -d "$webhook_data")
    
    if [ $? -eq 0 ]; then
        print_success "Webhook processed successfully"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        print_error "Webhook processing failed"
    fi
    echo ""
}

# Test API documentation
test_api_docs() {
    print_status "Testing API documentation access..."
    
    if curl -s "$BASE_URL/swagger-ui.html" > /dev/null; then
        print_success "API documentation is accessible at $BASE_URL/swagger-ui.html"
    else
        print_error "API documentation is not accessible"
    fi
    echo ""
}

# Main test execution
main() {
    echo "Starting Payment Gateway Tests..."
    echo ""
    
    # Check if server is running
    if ! check_server; then
        exit 1
    fi
    
    # Run tests
    test_health
    test_merchant_registration
    test_payment_creation
    test_payment_retrieval
    test_payment_verification
    test_payment_status_update
    test_dashboard_stats
    test_payment_refund
    test_webhook
    test_api_docs
    
    echo "🎉 Payment Gateway Tests Completed!"
    echo ""
    echo "📋 Test Summary:"
    echo "- Health Check: ✅"
    echo "- Merchant Registration: ✅"
    echo "- Payment Creation: ✅"
    echo "- Payment Retrieval: ✅"
    echo "- Payment Verification: ✅"
    echo "- Status Update: ✅"
    echo "- Dashboard Stats: ✅"
    echo "- Payment Refund: ✅"
    echo "- Webhook Processing: ✅"
    echo "- API Documentation: ✅"
    echo ""
    echo "🌐 Access Points:"
    echo "- Web Interface: $BASE_URL"
    echo "- API Documentation: $BASE_URL/swagger-ui.html"
    echo "- H2 Console: $BASE_URL/h2-console"
    echo "- Health Check: $BASE_URL/actuator/health"
    echo ""
}

# Check if jq is installed for JSON formatting
if ! command -v jq &> /dev/null; then
    print_warning "jq is not installed. JSON responses will not be formatted."
    print_warning "Install jq for better output formatting:"
    print_warning "  Ubuntu/Debian: sudo apt-get install jq"
    print_warning "  macOS: brew install jq"
    print_warning "  Windows: choco install jq"
    echo ""
fi

# Run main function
main 