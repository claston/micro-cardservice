package com.sistema.ledger.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Entry {
    private final UUID id;
    private final UUID tenantId;
    private final UUID transactionId;
    private final UUID ledgerAccountId;
    private final EntryDirection direction;
    private final Money money;
    private final Instant occurredAt;
    private final Instant createdAt;

    public Entry(UUID id,
                 UUID tenantId,
                 UUID transactionId,
                 UUID ledgerAccountId,
                 EntryDirection direction,
                 Money money,
                 Instant occurredAt,
                 Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId");
        this.ledgerAccountId = Objects.requireNonNull(ledgerAccountId, "ledgerAccountId");
        this.direction = Objects.requireNonNull(direction, "direction");
        this.money = Objects.requireNonNull(money, "money");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getLedgerAccountId() {
        return ledgerAccountId;
    }

    public EntryDirection getDirection() {
        return direction;
    }

    public Money getMoney() {
        return money;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
