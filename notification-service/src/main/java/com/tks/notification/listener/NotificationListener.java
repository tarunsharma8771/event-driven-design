package com.tks.notification.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Payment;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LogManager.getLogger(NotificationListener.class);

    private final Counter notificationSentCounter;
    private final Counter notificationFailureCounter;

    public NotificationListener(MeterRegistry meterRegistry) {
        this.notificationSentCounter = Counter.builder("orders.notification.sent")
                .description("Total order notifications sent")
                .tag("status", "success")
                .register(meterRegistry);
        this.notificationFailureCounter = Counter.builder("orders.notification.sent")
                .description("Total order notification attempts")
                .tag("status", "failed")
                .register(meterRegistry);
    }

    @RabbitListener(queues = RabbitTopology.PAYMENT_QUEUE)
    public void sendNotification(Payment payment) {
        try {
            notificationSentCounter.increment();
            log.info("Notification sent for order ID: {} with status: {}",
                    payment.getOrderId(),
                    payment.getStatus());
        } catch (RuntimeException e) {
            notificationFailureCounter.increment();
            throw e;
        }
    }
}
