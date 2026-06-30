package com.tks.payment.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import com.tks.common.model.Payment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private static final Logger log = LogManager.getLogger(PaymentListener.class);

    private final RabbitTemplate rabbitTemplate;

    public PaymentListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitTopology.ORDER_QUEUE)
    public void processOrder(Order order) {
        log.info("Initiating payment for order ID: {}", order.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        Payment payment = new Payment(order.getId(), "SUCCESS");

        rabbitTemplate.convertAndSend(
                RabbitTopology.PAYMENT_EXCHANGE,
                RabbitTopology.PAYMENT_PROCESSED_ROUTING_KEY,
                payment);
        log.info("Payment completed for order ID: {}", order.getId());
    }
}
