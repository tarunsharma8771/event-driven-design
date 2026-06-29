package com.tks.notification.listener;

import com.tks.notification.model.Payment;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @RabbitListener(queues = "paymentQueue")
    public void sendNotification(Payment payment) {
        System.out.println("Notification sent for order ID: "
                           + payment.getOrderId() 
                           + " has status " 
                           + payment.getStatus());
    }
}
