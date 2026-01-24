package com.sistema.ledger.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LedgerTransaction {
    private final UUID id;
    private final IdempotencyKey idempotencyKey;
    private final String externalReference;
    private final String description;
    private final Instant occurredAt;
    private final Instant createdAt;
    private final List<Entry> entries;

    public LedgerTransaction(UUID id,
                             IdempotencyKey idempotencyKey,
                             String externalReference,
                             String description,
                             Instant occurredAt,
                             Instant createdAt,
                             List<Entry> entries) {
        this.id = Objects.requireNonNull(id, "id");
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        this.externalReference = externalReference;
        this.description = description;
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.entries = List.copyOf(Objects.requireNonNull(entries, "entries"));
        if (this.entries.size() < 2) {
            throw new IllegalArgumentException("entries must have at least 2 items");
        }
    }

    public UUID getId() {
        return id;
    }

    public IdempotencyKey getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getDescription() {
        return description;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
