#!/bin/bash

# Payment Gateway API Testing Script
# This script tests the Payment Gateway API endpoints

BASE_URL="http://localhost:8080/api/payments"

echo "🧪 Payment Gateway API Testing"
echo "=============================="

# Function to check if the application is running
check_app_running() {
    echo "🔍 Checking if application is running..."
    if curl -s "$BASE_URL/health" > /dev/null; then
        echo "✅ Application is running!"
        return 0
    else
        echo "❌ Application is not running. Please start the application first."
        echo "   Run: ./run.sh"
        return 1
    fi
}

# Function to test health endpoint
test_health() {
    echo ""
    echo "🏥 Testing Health Endpoint..."
    response=$(curl -s "$BASE_URL/health")
    echo "Response: $response"
}

# Function to test API documentation endpoint
test_api_docs() {
    echo ""
    echo "📚 Testing API Documentation Endpoint..."
    response=$(curl -s "$BASE_URL/api-docs" | head -c 200)
    echo "Response (first 200 chars): $response..."
}

# Function to test dashboard stats endpoint
test_dashboard_stats() {
    echo ""
    echo "📊 Testing Dashboard Stats Endpoint..."
    response=$(curl -s "$BASE_URL/stats/dashboard")
    echo "Response: $response"
}

# Function to test payment creation
test_create_payment() {
    echo ""
    echo "💳 Testing Payment Creation..."
    
    payment_data='{
        "customerName": "Test Customer",
        "customerEmail": "test@example.com",
        "customerPhone": "+1234567890",
        "amount": 100.00,
        "currency": "USD",
        "description": "API Test Payment",
        "notes": "Created via API test script"
    }'
    
    response=$(curl -s -X POST "$BASE_URL/create" \
        -H "Content-Type: application/json" \
        -d "$payment_data")
    
    echo "Response: $response"
    
    # Extract payment ID for further tests
    payment_id=$(echo $response | grep -o '"id":[0-9]*' | cut -d':' -f2)
    if [ ! -z "$payment_id" ]; then
        echo "Payment ID: $payment_id"
        echo "$payment_id" > /tmp/payment_id.txt
    fi
}

# Function to test get payment by ID
test_get_payment() {
    echo ""
    echo "🔍 Testing Get Payment by ID..."
    
    if [ -f /tmp/payment_id.txt ]; then
        payment_id=$(cat /tmp/payment_id.txt)
        response=$(curl -s "$BASE_URL/$payment_id")
        echo "Response: $response"
    else
        echo "❌ No payment ID available. Run create payment test first."
    fi
}

# Function to test get payments by status
test_get_payments_by_status() {
    echo ""
    echo "📋 Testing Get Payments by Status..."
    
    response=$(curl -s "$BASE_URL/status/PENDING")
    echo "Pending Payments Response: $response"
}

# Function to test payment count by status
test_payment_count() {
    echo ""
    echo "📊 Testing Payment Count by Status..."
    
    response=$(curl -s "$BASE_URL/stats/count/PENDING")
    echo "Pending Payments Count: $response"
}

# Function to test invalid payment creation
test_invalid_payment() {
    echo ""
    echo "❌ Testing Invalid Payment Creation..."
    
    invalid_payment_data='{
        "customerName": "",
        "customerEmail": "invalid-email",
        "customerPhone": "123",
        "amount": -10.00,
        "currency": "INVALID"
    }'
    
    response=$(curl -s -X POST "$BASE_URL/create" \
        -H "Content-Type: application/json" \
        -d "$invalid_payment_data")
    
    echo "Response: $response"
}

# Function to test non-existent payment
test_non_existent_payment() {
    echo ""
    echo "🔍 Testing Get Non-existent Payment..."
    
    response=$(curl -s "$BASE_URL/999999")
    echo "Response: $response"
}

# Function to run all tests
run_all_tests() {
    echo "🚀 Running all API tests..."
    
    if ! check_app_running; then
        exit 1
    fi
    
    test_health
    test_api_docs
    test_dashboard_stats
    test_create_payment
    test_get_payment
    test_get_payments_by_status
    test_payment_count
    test_invalid_payment
    test_non_existent_payment
    
    echo ""
    echo "✅ All tests completed!"
    echo ""
    echo "📱 Web Interface: http://localhost:8080"
    echo "🗄️  H2 Database Console: http://localhost:8080/h2-console"
    echo "📚 API Documentation: http://localhost:8080/api/payments/api-docs"
}

# Function to show usage
show_usage() {
    echo ""
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  all          Run all tests (default)"
    echo "  health       Test health endpoint"
    echo "  create       Test payment creation"
    echo "  get          Test get payment"
    echo "  stats        Test statistics endpoints"
    echo "  invalid      Test invalid payment creation"
    echo "  help         Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 all       # Run all tests"
    echo "  $0 health    # Test health endpoint"
    echo "  $0 create    # Test payment creation"
}

# Main script logic
case "${1:-all}" in
    "all")
        run_all_tests
        ;;
    "health")
        check_app_running && test_health
        ;;
    "create")
        check_app_running && test_create_payment
        ;;
    "get")
        check_app_running && test_get_payment
        ;;
    "stats")
        check_app_running && test_dashboard_stats && test_payment_count
        ;;
    "invalid")
        check_app_running && test_invalid_payment
        ;;
    "help"|"-h"|"--help")
        show_usage
        ;;
    *)
        echo "❌ Unknown option: $1"
        show_usage
        exit 1
        ;;
esac 