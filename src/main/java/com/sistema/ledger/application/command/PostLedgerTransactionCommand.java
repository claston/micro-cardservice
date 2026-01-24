package com.sistema.ledger.application.command;

import java.time.Instant;
import java.util.List;

public class PostLedgerTransactionCommand {
    private final String idempotencyKey;
    private final String externalReference;
    private final String description;
    private final Instant occurredAt;
    private final List<PostingEntryCommand> entries;

    public PostLedgerTransactionCommand(String idempotencyKey,
                                        String externalReference,
                                        String description,
                                        Instant occurredAt,
                                        List<PostingEntryCommand> entries) {
        this.idempotencyKey = idempotencyKey;
        this.externalReference = externalReference;
        this.description = description;
        this.occurredAt = occurredAt;
        this.entries = entries;
    }

    public String getIdempotencyKey() {
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

    public List<PostingEntryCommand> getEntries() {
        return entries;
    }
}
