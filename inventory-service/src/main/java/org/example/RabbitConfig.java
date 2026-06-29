package org.example;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Declare inventory queue
    @Bean
    public Queue inventoryQueue() {
        return new Queue("inventoryQueue", true); // durable queue
    }

    // Declare the same exchange used by Order Service
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("orderExchange");
    }

    // Bind inventoryQueue to orderExchange with routing key "orderPlaced"
    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(inventoryQueue)
                             .to(orderExchange)
                             .with("orderPlaced");
    }
}
