package com.sistema.payments.application.command;

public class CreatePixPayoutCommand {
    private final String referenceType;
    private final String referenceId;
    private final long amountMinor;
    private final String currency;
    private final String pixKey;
    private final String debitFromWalletAccountId;
    private final String description;
    private final String idempotencyKey;

    public CreatePixPayoutCommand(String referenceType,
                                  String referenceId,
                                  long amountMinor,
                                  String currency,
                                  String pixKey,
                                  String debitFromWalletAccountId,
                                  String description,
                                  String idempotencyKey) {
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.pixKey = pixKey;
        this.debitFromWalletAccountId = debitFromWalletAccountId;
        this.description = description;
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

    public String getPixKey() {
        return pixKey;
    }

    public String getDebitFromWalletAccountId() {
        return debitFromWalletAccountId;
    }

    public String getDescription() {
        return description;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
