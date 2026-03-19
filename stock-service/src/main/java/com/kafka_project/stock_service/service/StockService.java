package com.kafka_project.stock_service.service;

import com.kafka_project.stock_service.entity.Stock;
import com.kafka_project.stock_service.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;

    public void deductStock(String productName, int quantity) {
        Optional<Stock> stockOptional = stockRepository.findByProductName(productName);

        if (stockOptional.isPresent()) {
            Stock stock = stockOptional.get();
            int currentQuantity = stock.getAvailableQuantity();

            if (currentQuantity >= quantity) {
                stock.setAvailableQuantity(currentQuantity - quantity);
                stockRepository.save(stock);
                LOGGER.info("Stock deducted successfully. Product: {}, Deducted: {}, Remaining: {}",
                    productName, quantity, stock.getAvailableQuantity());
            } else {
                LOGGER.warn("Insufficient stock. Product: {}, Available: {}, Requested: {}",
                    productName, currentQuantity, quantity);
            }
        } else {
            LOGGER.warn("Product not found in stock: {}", productName);
        }
    }

    public Stock addStock(String productName, int quantity) {
        Optional<Stock> stockOptional = stockRepository.findByProductName(productName);

        if (stockOptional.isPresent()) {
            Stock stock = stockOptional.get();
            stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
            Stock savedStock = stockRepository.save(stock);
            LOGGER.info("Stock added. Product: {}, Added: {}, New Total: {}",
                productName, quantity, savedStock.getAvailableQuantity());
            return savedStock;
        } else {
            Stock newStock = new Stock();
            newStock.setProductName(productName);
            newStock.setAvailableQuantity(quantity);
            Stock savedStock = stockRepository.save(newStock);
            LOGGER.info("New stock created. Product: {}, Quantity: {}",
                productName, quantity);
            return savedStock;
        }
    }

    public Optional<Stock> getStock(String productName) {
        return stockRepository.findByProductName(productName);
    }

    public java.util.List<Stock> getAllStock() {
        return stockRepository.findAll();
    }
}
