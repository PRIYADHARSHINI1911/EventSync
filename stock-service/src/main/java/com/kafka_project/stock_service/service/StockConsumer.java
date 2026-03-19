package com.kafka_project.stock_service.service;

import com.kafka_project.stock_service.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockConsumer.class);

    @Autowired
    private StockService stockService;

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderEvent orderEvent) {
        LOGGER.info("Order event received in Stock Service: {}", orderEvent.getMessage());
        LOGGER.info("Order ID: {}, Product: {}, Quantity: {}", 
            orderEvent.getOrder().getOrderId(),
            orderEvent.getOrder().getName(),
            orderEvent.getOrder().getQuantity());
        
        // Process the order event - e.g., update stock, validate inventory, etc.
        updateStock(orderEvent);
    }

    private void updateStock(OrderEvent orderEvent) {
        LOGGER.info("Updating stock for order #{}: {} x {} units",
            orderEvent.getOrder().getOrderId(),
            orderEvent.getOrder().getName(),
            orderEvent.getOrder().getQuantity());
        
        // Deduct quantity from inventory
        stockService.deductStock(orderEvent.getOrder().getName(), orderEvent.getOrder().getQuantity());
    }
}
