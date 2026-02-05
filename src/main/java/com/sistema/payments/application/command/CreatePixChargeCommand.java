package com.sistema.payments.application.command;

public class CreatePixChargeCommand {
    private final String referenceType;
    private final String referenceId;
    private final long amountMinor;
    private final String currency;
    private final String payerName;
    private final String payerDocument;
    private final String creditToWalletAccountId;
    private final String idempotencyKey;

    public CreatePixChargeCommand(String referenceType,
                                  String referenceId,
                                  long amountMinor,
                                  String currency,
                                  String payerName,
                                  String payerDocument,
                                  String creditToWalletAccountId,
                                  String idempotencyKey) {
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.payerName = payerName;
        this.payerDocument = payerDocument;
        this.creditToWalletAccountId = creditToWalletAccountId;
        this.idempotencyKey = idempotencyKey;
    }

    public String getReferenceType() {
        return referenceType;
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

    public String getCreditToWalletAccountId() {
        return creditToWalletAccountId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
