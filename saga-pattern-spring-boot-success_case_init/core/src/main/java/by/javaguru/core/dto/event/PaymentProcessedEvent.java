package by.javaguru.core.dto.event;

import java.util.UUID;

public class PaymentProcessedEvent {

    private UUID orderId;
    private UUID paymentId;

    public PaymentProcessedEvent(UUID orderId, UUID paymentId) {
        this.orderId = orderId;
        this.paymentId = paymentId;
    }

    public PaymentProcessedEvent() {
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }
}
