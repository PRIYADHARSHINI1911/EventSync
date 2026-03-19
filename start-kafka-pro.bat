@echo off
REM Kafka Pro - Quick Start Script for Windows

echo.
echo ========================================
echo   Kafka Pro - Quick Start
echo ========================================
echo.

REM Check if Docker is running
docker ps >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [0/5] Cleaning up old containers...
docker-compose -f Kafka/docker-compose.yaml down 2>nul
timeout /t 2 /nobreak >nul

echo [1/5] Starting Kafka, Zookeeper, PostgreSQL, and pgAdmin...
cd Kafka
docker-compose up -d
if errorlevel 1 (
    echo [ERROR] Failed to start Docker services.
    pause
    exit /b 1
)
cd ..

echo [2/5] Waiting for PostgreSQL to be ready (this may take 30-60 seconds)...
REM Wait for PostgreSQL to be fully ready
setlocal enabledelayedexpansion
set /a counter=0
:wait_for_postgres
docker exec postgres_db pg_isready -U admin -d kafka_pro_db >nul 2>&1
if %errorlevel% neq 0 (
    set /a counter+=1
    if !counter! leq 120 (
        set /a display=!counter!/2
        echo   Waiting for PostgreSQL... (!display!/60 seconds)
        timeout /t 1 /nobreak >nul
        goto wait_for_postgres
    ) else (
        echo [ERROR] PostgreSQL did not start within 120 seconds
        docker logs postgres_db
        pause
        exit /b 1
    )
)
echo   PostgreSQL is ready!
endlocal

echo [3/5] Starting Stock Service in background...
cd stock-service
start "Stock Service" cmd /k "call mvnw clean spring-boot:run"
cd ..
timeout /t 5 /nobreak

echo [4/5] Waiting for Stock Service to initialize...
timeout /t 10 /nobreak

echo [5/5] Starting Order Service...
cd order-service
call mvnw clean spring-boot:run

echo.
echo ========================================
echo   Services are running!
echo   Order Service UI: http://localhost:9000
echo   Stock Service: http://localhost:9001/api/stock
echo   pgAdmin: http://localhost:5050
echo   PostgreSQL: localhost:5432
echo   Credentials: admin / admin123
echo ========================================
echo.

