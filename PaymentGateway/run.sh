#!/bin/bash

# Payment Gateway Runner Script
# This script helps you run the Payment Gateway application

echo "🚀 Payment Gateway - Starting Application"
echo "=========================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Error: Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo "✅ Maven version: $(mvn -version | head -n 1)"

# Function to show usage
show_usage() {
    echo ""
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  run          Run the application (default)"
    echo "  build        Build the application"
    echo "  test         Run tests"
    echo "  clean        Clean build artifacts"
    echo "  docker       Run with Docker"
    echo "  help         Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 run       # Run the application"
    echo "  $0 build     # Build the application"
    echo "  $0 test      # Run tests"
    echo "  $0 docker    # Run with Docker"
}

# Function to run the application
run_app() {
    echo "🏗️  Building application..."
    mvn clean compile
    
    echo "🚀 Starting Payment Gateway..."
    echo "📱 Web Interface: http://localhost:8080"
    echo "🗄️  H2 Database Console: http://localhost:8080/h2-console"
    echo "📚 API Documentation: http://localhost:8080/api/payments/api-docs"
    echo "💚 Health Check: http://localhost:8080/api/payments/health"
    echo ""
    echo "Press Ctrl+C to stop the application"
    echo ""
    
    mvn spring-boot:run
}

# Function to build the application
build_app() {
    echo "🏗️  Building application..."
    mvn clean package -DskipTests
    echo "✅ Build completed successfully!"
}

# Function to run tests
run_tests() {
    echo "🧪 Running tests..."
    mvn test
    echo "✅ Tests completed!"
}

# Function to clean build artifacts
clean_app() {
    echo "🧹 Cleaning build artifacts..."
    mvn clean
    echo "✅ Clean completed!"
}

# Function to run with Docker
run_docker() {
    echo "🐳 Running with Docker..."
    
    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        echo "❌ Error: Docker is not installed. Please install Docker."
        exit 1
    fi
    
    # Check if Docker Compose is installed
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Error: Docker Compose is not installed. Please install Docker Compose."
        exit 1
    fi
    
    echo "✅ Docker version: $(docker --version)"
    echo "✅ Docker Compose version: $(docker-compose --version)"
    
    echo "🏗️  Building and starting containers..."
    docker-compose up --build
    
    echo "📱 Web Interface: http://localhost:8080"
    echo "🗄️  H2 Database Console: http://localhost:8080/h2-console"
    echo "📚 API Documentation: http://localhost:8080/api/payments/api-docs"
}

# Main script logic
case "${1:-run}" in
    "run")
        run_app
        ;;
    "build")
        build_app
        ;;
    "test")
        run_tests
        ;;
    "clean")
        clean_app
        ;;
    "docker")
        run_docker
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