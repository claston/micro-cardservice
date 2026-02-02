package com.sistema.ledger.application.model;

import com.sistema.ledger.domain.model.LedgerTransaction;

public class PostLedgerTransactionResult {
    private final LedgerTransaction transaction;
    private final boolean idempotentReplay;

    public PostLedgerTransactionResult(LedgerTransaction transaction, boolean idempotentReplay) {
        this.transaction = transaction;
        this.idempotentReplay = idempotentReplay;
    }

    public LedgerTransaction getTransaction() {
        return transaction;
    }

    public boolean isIdempotentReplay() {
        return idempotentReplay;
    }
}
