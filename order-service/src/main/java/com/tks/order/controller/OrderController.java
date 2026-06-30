package com.tks.order.controller;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LogManager.getLogger(OrderController.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public String placeOrder(@RequestBody Order order) {
        order.setId(UUID.randomUUID().toString());
        log.info("Order received with ID: {}", order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        rabbitTemplate.convertAndSend(
                RabbitTopology.ORDER_EXCHANGE,
                RabbitTopology.ORDER_PLACED_ROUTING_KEY,
                order);
        log.info("OrderPlaced event published for order ID: {}", order.getId());
        return "Order placed with ID: " + order.getId();
    }
}
