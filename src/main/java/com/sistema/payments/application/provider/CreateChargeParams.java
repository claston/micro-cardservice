package com.sistema.payments.application.provider;

public class CreateChargeParams {
    private final String referenceId;
    private final long amountMinor;
    private final String currency;
    private final String payerName;
    private final String payerDocument;

    public CreateChargeParams(String referenceId,
                              long amountMinor,
                              String currency,
                              String payerName,
                              String payerDocument) {
        this.referenceId = referenceId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.payerName = payerName;
        this.payerDocument = payerDocument;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPayerName() {
        return payerName;
    }

    public String getPayerDocument() {
        return payerDocument;
    }
}
