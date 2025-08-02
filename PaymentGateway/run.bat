@echo off
REM Payment Gateway Runner Script for Windows
REM This script helps you run the Payment Gateway application

echo 🚀 Payment Gateway - Starting Application
echo ==========================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Java is not installed. Please install Java 17 or higher.
    pause
    exit /b 1
)

echo ✅ Java version:
java -version

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Maven is not installed. Please install Maven 3.6 or higher.
    pause
    exit /b 1
)

echo ✅ Maven version:
mvn -version

REM Function to show usage
:show_usage
if "%1"=="help" goto usage
if "%1"=="-h" goto usage
if "%1"=="--help" goto usage
goto :eof

:usage
echo.
echo Usage: %0 [OPTION]
echo.
echo Options:
echo   run          Run the application (default)
echo   build        Build the application
echo   test         Run tests
echo   clean        Clean build artifacts
echo   docker       Run with Docker
echo   help         Show this help message
echo.
echo Examples:
echo   %0 run       # Run the application
echo   %0 build     # Build the application
echo   %0 test      # Run tests
echo   %0 docker    # Run with Docker
pause
exit /b 0

REM Function to run the application
:run_app
echo 🏗️  Building application...
call mvn clean compile

echo 🚀 Starting Payment Gateway...
echo 📱 Web Interface: http://localhost:8080
echo 🗄️  H2 Database Console: http://localhost:8080/h2-console
echo 📚 API Documentation: http://localhost:8080/api/payments/api-docs
echo 💚 Health Check: http://localhost:8080/api/payments/health
echo.
echo Press Ctrl+C to stop the application
echo.

call mvn spring-boot:run
goto :eof

REM Function to build the application
:build_app
echo 🏗️  Building application...
call mvn clean package -DskipTests
echo ✅ Build completed successfully!
pause
exit /b 0

REM Function to run tests
:run_tests
echo 🧪 Running tests...
call mvn test
echo ✅ Tests completed!
pause
exit /b 0

REM Function to clean build artifacts
:clean_app
echo 🧹 Cleaning build artifacts...
call mvn clean
echo ✅ Clean completed!
pause
exit /b 0

REM Function to run with Docker
:run_docker
echo 🐳 Running with Docker...

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Docker is not installed. Please install Docker.
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Docker Compose is not installed. Please install Docker Compose.
    pause
    exit /b 1
)

echo ✅ Docker version:
docker --version
echo ✅ Docker Compose version:
docker-compose --version

echo 🏗️  Building and starting containers...
docker-compose up --build

echo 📱 Web Interface: http://localhost:8080
echo 🗄️  H2 Database Console: http://localhost:8080/h2-console
echo 📚 API Documentation: http://localhost:8080/api/payments/api-docs
pause
exit /b 0

REM Main script logic
if "%1"=="" goto run_app
if "%1"=="run" goto run_app
if "%1"=="build" goto build_app
if "%1"=="test" goto run_tests
if "%1"=="clean" goto clean_app
if "%1"=="docker" goto run_docker
if "%1"=="help" goto usage
if "%1"=="-h" goto usage
if "%1"=="--help" goto usage

echo ❌ Unknown option: %1
goto usage 