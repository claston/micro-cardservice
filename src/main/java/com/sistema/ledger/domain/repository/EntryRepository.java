package com.sistema.ledger.domain.repository;

import com.sistema.ledger.application.model.StatementPage;

import java.time.Instant;
import java.util.UUID;

public interface EntryRepository {
    long getBalanceMinor(UUID tenantId, UUID ledgerAccountId);

    StatementPage getStatement(UUID tenantId, UUID ledgerAccountId, Instant from, Instant to, int page, int size);
}
