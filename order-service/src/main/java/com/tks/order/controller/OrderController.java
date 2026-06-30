package com.tks.order.controller;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;

    public OrderController(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.meterRegistry = meterRegistry;
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
        Counter.builder("orders.placed")
                .description("Total orders placed")
                .tag("product", productTag(order))
                .register(meterRegistry)
                .increment();
        log.info("OrderPlaced event published for order ID: {}", order.getId());
        return "Order placed with ID: " + order.getId();
    }

    private String productTag(Order order) {
        if (order.getProduct() == null || order.getProduct().isBlank()) {
            return "unknown";
        }
        return order.getProduct().trim().toLowerCase().replaceAll("[^a-z0-9_-]", "_");
    }
}
