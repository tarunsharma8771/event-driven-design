package org.example;

import org.example.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final RabbitTemplate rabbitTemplate;

    @Value("${order.exchange:orderExchange}")
    private String exchange;

    @Value("${order.routingKey:orderPlaced}")
    private String routingKey;

    public OrderController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public String placeOrder(@RequestBody Order order) {
        order.setId(UUID.randomUUID().toString());
        System.out.println("Order received with ID: " + order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        rabbitTemplate.convertAndSend(exchange, routingKey, order);
        return "Order placed with ID: " + order.getId();
    }
}
