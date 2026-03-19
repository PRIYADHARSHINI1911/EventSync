# Kafka Pro - Order UI Setup Guide

A simple web UI for the Kafka order management system built with Spring Boot and Kafka.

## Features

- **Create Orders**: Submit new orders through a clean web interface
- **View Orders**: See all orders with real-time updates
- **Order Statistics**: Track total orders and total value
- **Delete Orders**: Remove orders from the system
- **Auto-refresh**: Orders list refreshes every 5 seconds

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose
- Modern web browser

## Quick Start

### 1. Start Kafka and Zookeeper

Navigate to the project root and run:

```bash
docker-compose -f Kafka/docker-compose.yaml up -d
```

This will start:
- Kafka broker on `localhost:9092`
- Zookeeper on `localhost:2181`

### 2. Build and Run Order Service

Navigate to the order-service directory:

```bash
cd order-service
```

**On Windows (using Maven wrapper):**
```bash
mvnw clean spring-boot:run
```

**On macOS/Linux:**
```bash
./mvnw clean spring-boot:run
```

The service will start on `http://localhost:8080`

### 3. Access the UI

Open your browser and navigate to:

```
http://localhost:8080
```

## Usage

### Creating an Order

1. Fill in the "Create New Order" form with:
   - **Product Name**: Name of the product (e.g., Laptop, Phone)
   - **Quantity**: Number of units
   - **Price**: Price per unit

2. Click "Create Order" button

3. The order will be:
   - Saved to the database
   - Published to Kafka topic `order-events`
   - Displayed in the orders list

### Viewing Orders

- Orders automatically appear in the "Recent Orders" section
- Each order shows:
  - Order ID
  - Product name
  - Quantity and unit price
  - Total value
  - Status (currently shows "PENDING")
  - Delete button

### Monitoring Statistics

The statistics panel shows:
- **Total Orders**: Count of all orders
- **Total Value**: Sum of all order values (quantity × price)

Both update automatically when orders are created or deleted.

## API Endpoints

The backend provides REST endpoints for managing orders:

```
POST   /api/orders              - Create a new order
GET    /api/orders              - Get all orders
GET    /api/orders/{id}         - Get a specific order
DELETE /api/orders/{id}         - Delete an order
```

### Example: Create Order via cURL

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "quantity": 5,
    "price": 999.99
  }'
```

## Architecture

```
┌─────────────────────────────────────┐
│      Web UI (HTML/CSS/JS)           │
│   - Create orders                   │
│   - View orders                     │
│   - Statistics                      │
└────────────┬────────────────────────┘
             │
             │ HTTP REST API
             │
┌────────────▼────────────────────────┐
│   OrderServiceApplication           │
│   - OrderController (REST API)      │
│   - OrderService (Business Logic)   │
│   - OrderProducer (Kafka Publishing)│
└────────────┬────────────────────────┘
             │
    ┌────────┴──────────┐
    │                   │
    ▼                   ▼
┌─────────┐         ┌──────────┐
│   H2    │         │  Kafka   │
│Database │         │ Broker   │
└─────────┘         └──────────┘
```

## Project Structure

```
order-service/
├── src/main/java/com/kafka_project/order_service/
│   ├── OrderServiceApplication.java
│   ├── controller/
│   │   └── OrderController.java       ← REST endpoints
│   ├── service/
│   │   └── OrderProducer.java         ← Kafka producer
│   ├── config/
│   │   └── KafkaTopicConfig.java
│   ├── dto/
│   │   └── OrderEvent.java
│   ├── entity/
│   │   └── Order.java
│   └── repository/
│       └── OrderRepository.java
├── src/main/resources/
│   ├── application.properties         ← Configuration
│   └── static/
│       └── index.html                 ← Web UI
└── pom.xml
```

## Configuration

### Database
- Type: H2 (in-memory)
- Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`

### Kafka
- Bootstrap Servers: `localhost:9092`
- Topic: `order-events`
- Serialization: JSON

## Troubleshooting

### Kafka Connection Issues

If you get connection errors:

1. Verify Kafka is running:
   ```bash
   docker-compose -f Kafka/docker-compose.yaml logs kafka
   ```

2. Check if port 9092 is accessible:
   ```bash
   telnet localhost 9092
   ```

### UI Not Loading

- Ensure the service is running on port 8080
- Check browser console for errors (F12)
- Clear browser cache and refresh

### Orders Not Saving

1. Check if H2 database is accessible
2. View H2 console at `http://localhost:8080/h2-console`
3. Check application logs for errors

## Next Steps

- Add order status updates through Kafka consumers
- Create a stock-service consumer to track inventory
- Add WebSocket support for real-time updates
- Implement user authentication and authorization
- Add order history and filtering capabilities
- Create a dashboard with advanced analytics

## Notes

- All orders are stored in an in-memory H2 database (cleared when service restarts)
- Service uses localhost:8080, ensure this port is available
- Kafka topic is auto-created if it doesn't exist
- CORS is enabled for the API to allow requests from the UI

## Support

For issues or questions, check:
1. Application logs in terminal
2. Kafka broker logs: `docker-compose logs kafka`
3. H2 console database state
