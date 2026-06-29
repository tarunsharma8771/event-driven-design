package com.tks.payment.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import com.tks.common.model.Payment;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private final RabbitTemplate rabbitTemplate;

    public PaymentListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitTopology.ORDER_QUEUE)
    public void processOrder(Order order) {
        System.out.println("Initiating payment for order ID: " + order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Payment payment = new Payment(order.getId(), "SUCCESS");

        rabbitTemplate.convertAndSend(
                RabbitTopology.PAYMENT_EXCHANGE,
                RabbitTopology.PAYMENT_PROCESSED_ROUTING_KEY,
                payment);
        System.out.println("Payment completed for order ID: " + order.getId());
    }
}
