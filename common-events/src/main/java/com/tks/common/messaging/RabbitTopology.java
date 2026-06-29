package com.tks.common.messaging;

public final class RabbitTopology {
    public static final String ORDER_EXCHANGE = "orderExchange";
    public static final String ORDER_QUEUE = "orderQueue";
    public static final String ORDER_PLACED_ROUTING_KEY = "orderPlaced";

    public static final String INVENTORY_QUEUE = "inventoryQueue";

    public static final String PAYMENT_EXCHANGE = "paymentExchange";
    public static final String PAYMENT_QUEUE = "paymentQueue";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "paymentProcessed";

    private RabbitTopology() {
    }
}
