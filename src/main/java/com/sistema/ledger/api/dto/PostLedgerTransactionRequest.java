package com.sistema.ledger.api.dto;

import java.time.Instant;
import java.util.List;

public class PostLedgerTransactionRequest {
    private String idempotencyKey;
    private String externalReference;
    private String description;
    private Instant occurredAt;
    private List<LedgerEntryRequest> entries;

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public List<LedgerEntryRequest> getEntries() {
        return entries;
    }

    public void setEntries(List<LedgerEntryRequest> entries) {
        this.entries = entries;
    }
}
