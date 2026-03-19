-- Initialize Kafka Pro database
CREATE DATABASE IF NOT EXISTS kafka_pro_db;

-- Create tables for order-service
CREATE TABLE IF NOT EXISTS orders (
    order_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL
);

-- Create tables for stock-service
CREATE TABLE IF NOT EXISTS stock (
    stock_id SERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL UNIQUE,
    available_quantity INTEGER NOT NULL
);

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE kafka_pro_db TO admin;
