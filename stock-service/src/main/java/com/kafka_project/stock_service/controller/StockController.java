package com.kafka_project.stock_service.controller;

import com.kafka_project.stock_service.entity.Stock;
import com.kafka_project.stock_service.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "*")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping
    public ResponseEntity<?> addStock(@RequestBody Stock stock) {
        try {
            Stock savedStock = stockService.addStock(stock.getProductName(), stock.getAvailableQuantity());
            return ResponseEntity.ok(savedStock);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding stock: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllStock() {
        try {
            java.util.List<Stock> stocks = stockService.getAllStock();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving stock: " + e.getMessage());
        }
    }

    @GetMapping("/{productName}")
    public ResponseEntity<?> getStockByProduct(@PathVariable String productName) {
        try {
            var stock = stockService.getStock(productName);
            if (stock.isPresent()) {
                return ResponseEntity.ok(stock.get());
            }
            return ResponseEntity.status(404).body("Stock not found for product: " + productName);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving stock: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        try {
            stock.setStockId(id);
            // TODO: implement update logic in service
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating stock: " + e.getMessage());
        }
    }
}
