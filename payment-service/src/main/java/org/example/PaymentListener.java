package org.example;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private final RabbitTemplate rabbitTemplate;

    public PaymentListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "orderQueue")
    public void processOrder(Order order) {
        System.out.println("Initiating payment for order ID: " + order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Payment payment = new Payment(order.getId(), "SUCCESS");

        rabbitTemplate.convertAndSend("paymentExchange", "paymentProcessed", payment);
        System.out.println("Payment completed for order ID: " + order.getId());
    }
}
