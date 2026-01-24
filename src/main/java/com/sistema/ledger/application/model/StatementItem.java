package com.sistema.ledger.application.model;

import com.sistema.ledger.domain.model.EntryDirection;

import java.time.Instant;
import java.util.UUID;

public class StatementItem {
    private final Instant occurredAt;
    private final UUID transactionId;
    private final String description;
    private final EntryDirection direction;
    private final long amountMinor;
    private final String currency;

    public StatementItem(Instant occurredAt,
                         UUID transactionId,
                         String description,
                         EntryDirection direction,
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

    public EntryDirection getDirection() {
        return direction;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public String getCurrency() {
        return currency;
    }
}
