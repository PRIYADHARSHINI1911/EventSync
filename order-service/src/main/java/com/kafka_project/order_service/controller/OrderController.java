package com.kafka_project.order_service.controller;

import com.kafka_project.order_service.dto.OrderEvent;
import com.kafka_project.order_service.entity.Order;
import com.kafka_project.order_service.repository.OrderRepository;
import com.kafka_project.order_service.service.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            // Save order to database
            Order savedOrder = orderRepository.save(order);

            // Create and send order event to Kafka
            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setMessage("New order created");
            orderEvent.setStatus("PENDING");
            orderEvent.setOrder(savedOrder);
            orderProducer.sendOrderEvent(orderEvent);

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        var order = orderRepository.findById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        }
        return ResponseEntity.status(404).body("Order not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok("Order deleted successfully");
        }
        return ResponseEntity.status(404).body("Order not found");
    }
}
