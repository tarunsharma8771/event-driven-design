package com.tks.notification.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Payment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LogManager.getLogger(NotificationListener.class);

    @RabbitListener(queues = RabbitTopology.PAYMENT_QUEUE)
    public void sendNotification(Payment payment) {
        log.info("Notification sent for order ID: {} with status: {}",
                payment.getOrderId(),
                payment.getStatus());
    }
}
