package com.sistema.ledger.domain.repository;

import com.sistema.ledger.domain.model.LedgerTransaction;

import java.util.Optional;

public interface LedgerTransactionRepository {
    Optional<LedgerTransaction> findByIdempotencyKey(java.util.UUID tenantId, String idempotencyKey);

    LedgerTransaction save(LedgerTransaction transaction);
}
