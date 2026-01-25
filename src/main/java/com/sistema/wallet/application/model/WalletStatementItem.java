package com.sistema.wallet.application.model;

import java.time.Instant;
import java.util.UUID;

public class WalletStatementItem {
    private final Instant occurredAt;
    private final UUID transactionId;
    private final String description;
    private final String direction;
    private final long amountMinor;
    private final String currency;

    public WalletStatementItem(Instant occurredAt,
                               UUID transactionId,
                               String description,
                               String direction,
                               long amountMinor,
                               String currency) {
        this.occurredAt = occurredAt;
        this.transactionId = transactionId;
        this.description = description;
        this.direction = direction;
        this.amountMinor = amountMinor;
        this.currency = currency;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getDescription() {
        return description;
    }

    public String getDirection() {
        return direction;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public String getCurrency() {
        return currency;
    }
}
