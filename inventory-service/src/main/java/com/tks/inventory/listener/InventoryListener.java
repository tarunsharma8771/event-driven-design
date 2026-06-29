package com.tks.inventory.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

    @RabbitListener(queues = RabbitTopology.INVENTORY_QUEUE)
    public void reserveStock(Order order) {
        System.out.println("Reserving order with ID: " + order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
