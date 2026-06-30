package com.tks.payment.listener;

import com.tks.common.messaging.RabbitTopology;
import com.tks.common.model.Order;
import com.tks.common.model.Payment;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private static final Logger log = LogManager.getLogger(PaymentListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final Counter paymentSuccessCounter;
    private final Counter paymentFailureCounter;

    public PaymentListener(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.paymentSuccessCounter = Counter.builder("orders.payment.processed")
                .description("Total payment processing attempts")
                .tag("status", "success")
                .register(meterRegistry);
        this.paymentFailureCounter = Counter.builder("orders.payment.processed")
                .description("Total payment processing attempts")
                .tag("status", "failed")
                .register(meterRegistry);
    }

    @RabbitListener(queues = RabbitTopology.ORDER_QUEUE)
    public void processOrder(Order order) {
        try {
            log.info("Initiating payment for order ID: {}", order.getId());
            Thread.sleep(1000);
            Payment payment = new Payment(order.getId(), "SUCCESS");

            rabbitTemplate.convertAndSend(
                    RabbitTopology.PAYMENT_EXCHANGE,
                    RabbitTopology.PAYMENT_PROCESSED_ROUTING_KEY,
                    payment);
            paymentSuccessCounter.increment();
            log.info("Payment completed for order ID: {}", order.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            paymentFailureCounter.increment();
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            paymentFailureCounter.increment();
            throw e;
        }
    }
}
