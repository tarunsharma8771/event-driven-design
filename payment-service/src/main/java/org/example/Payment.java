package org.example;

import java.io.Serializable;

public class Payment implements Serializable {
    private String orderId;
    private String status;

    public Payment() {}

    public Payment(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
