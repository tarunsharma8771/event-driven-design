package org.example;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

;

@Component
public class InventoryListener {

    // Listen to inventoryQueue
    @RabbitListener(queues = "inventoryQueue")
    public void reserveStock(Order order) {
        System.out.println("Reserving order with ID: " + order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Add logic here to update inventory DB or service
    }
}
