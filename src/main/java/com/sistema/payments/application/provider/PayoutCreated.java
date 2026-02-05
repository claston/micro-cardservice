package com.sistema.payments.application.provider;

public class PayoutCreated {
    private final String externalPaymentId;

    public PayoutCreated(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }
}
