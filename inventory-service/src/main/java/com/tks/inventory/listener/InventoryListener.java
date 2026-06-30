package com.tks.inventory.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

    private static final Logger log = LogManager.getLogger(InventoryListener.class);

    @RabbitListener(queues = RabbitTopology.INVENTORY_QUEUE)
    public void reserveStock(Order order) {
        log.info("Reserving stock for order ID: {}", order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        log.info("Stock reserved for order ID: {}", order.getId());
    }
}
