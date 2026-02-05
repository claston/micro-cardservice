package com.sistema.payments.application.model;

import java.util.UUID;

public class PixPayoutResult {
    private final UUID paymentId;
    private final String status;
    private final String externalPaymentId;

    public PixPayoutResult(UUID paymentId, String status, String externalPaymentId) {
        this.paymentId = paymentId;
        this.status = status;
        this.externalPaymentId = externalPaymentId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }
}
