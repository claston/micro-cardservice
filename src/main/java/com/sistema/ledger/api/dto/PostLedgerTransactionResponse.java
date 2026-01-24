package com.sistema.ledger.api.dto;

import java.util.UUID;

public class PostLedgerTransactionResponse {
    private UUID transactionId;

    public PostLedgerTransactionResponse() {
    }

    public PostLedgerTransactionResponse(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }
}
