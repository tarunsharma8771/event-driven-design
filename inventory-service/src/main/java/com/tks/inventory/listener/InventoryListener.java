package com.tks.inventory.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

    private static final Logger log = LogManager.getLogger(InventoryListener.class);

    private final Counter inventoryReservedCounter;
    private final Counter inventoryFailureCounter;

    public InventoryListener(MeterRegistry meterRegistry) {
        this.inventoryReservedCounter = Counter.builder("orders.inventory.reserved")
                .description("Total orders with reserved inventory")
                .tag("status", "success")
                .register(meterRegistry);
        this.inventoryFailureCounter = Counter.builder("orders.inventory.reserved")
                .description("Total inventory reservation attempts")
                .tag("status", "failed")
                .register(meterRegistry);
    }

    @RabbitListener(queues = RabbitTopology.INVENTORY_QUEUE)
    public void reserveStock(Order order) {
        try {
            log.info("Reserving stock for order ID: {}", order.getId());
            Thread.sleep(1000);
            inventoryReservedCounter.increment();
            log.info("Stock reserved for order ID: {}", order.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            inventoryFailureCounter.increment();
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            inventoryFailureCounter.increment();
            throw e;
        }
    }
}
