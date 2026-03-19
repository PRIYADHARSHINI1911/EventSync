package com.kafka_project.order_service.service;

import com.kafka_project.order_service.dto.OrderEvent;
import com.kafka_project.order_service.entity.Order;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);
    @Autowired
    private NewTopic topic;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private void send(OrderEvent orderEvent){
        LOGGER.info("Sending order event: {}",orderEvent);

        Message<OrderEvent> message = MessageBuilder
                .withPayload(orderEvent)
                .setHeader(KafkaHeaders.TOPIC,topic.name())
                .build();
        kafkaTemplate.send(message);
    }

    public void sendOrderEvent(OrderEvent orderEvent) {
        send(orderEvent);
    }

}
