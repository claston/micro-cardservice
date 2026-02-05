package com.sistema.payments.application.provider;

public class CreatePayoutParams {
    private final String referenceId;
    private final long amountMinor;
    private final String currency;
    private final String pixKey;
    private final String description;

    public CreatePayoutParams(String referenceId,
                              long amountMinor,
                              String currency,
                              String pixKey,
                              String description) {
        this.referenceId = referenceId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.pixKey = pixKey;
        this.description = description;
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

    public String getPixKey() {
        return pixKey;
    }

    public String getDescription() {
        return description;
    }
}
