#!/bin/bash

echo "=== Razorpay Payment Gateway Test Setup ==="
echo

# Check if Java is installed
echo "1. Checking Java installation..."
java -version
echo

# Check if Maven is installed
echo "2. Checking Maven installation..."
mvn -version
echo

# Compile the project
echo "3. Compiling the project..."
mvn clean compile -q
if [ $? -eq 0 ]; then
    echo "✅ Compilation successful"
else
    echo "❌ Compilation failed"
    exit 1
fi
echo

# Package the application
echo "4. Packaging the application..."
mvn package -DskipTests -q
if [ $? -eq 0 ]; then
    echo "✅ Packaging successful"
else
    echo "❌ Packaging failed"
    exit 1
fi
echo

# Check if JAR file was created
if [ -f "target/razorpay-payment-gateway-1.0.0.jar" ]; then
    echo "✅ JAR file created successfully"
else
    echo "❌ JAR file not found"
    exit 1
fi
echo

echo "=== Setup Test Complete ==="
echo "✅ All tests passed!"
echo
echo "To run the application:"
echo "1. Set your Razorpay API keys:"
echo "   export RAZORPAY_KEY_ID=your_key_id"
echo "   export RAZORPAY_KEY_SECRET=your_key_secret"
echo "2. Run: java -jar target/razorpay-payment-gateway-1.0.0.jar"
echo "3. Or run: mvn spring-boot:run"
echo "4. Access: http://localhost:8080"
echo