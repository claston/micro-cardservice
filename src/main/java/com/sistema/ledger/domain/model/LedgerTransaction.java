package com.sistema.ledger.domain.model;

import com.sistema.ledger.domain.validation.LedgerPostingPolicy;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LedgerTransaction {
    private final UUID id;
    private final UUID tenantId;
    private final IdempotencyKey idempotencyKey;
    private final String externalReference;
    private final String description;
    private final Instant occurredAt;
    private final Instant createdAt;
    private final List<Entry> entries;

    public LedgerTransaction(UUID id,
                             UUID tenantId,
                             IdempotencyKey idempotencyKey,
                             String externalReference,
                             String description,
                             Instant occurredAt,
                             Instant createdAt,
                             List<Entry> entries) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        this.externalReference = externalReference;
        this.description = description;
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.entries = List.copyOf(Objects.requireNonNull(entries, "entries"));
        new LedgerPostingPolicy().validateDoubleEntry(this.entries);
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
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
