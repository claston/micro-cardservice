package com.sistema.ledger.domain.repository;

import com.sistema.ledger.domain.model.LedgerAccount;

import java.util.Optional;
import java.util.UUID;

public interface LedgerAccountRepository {
    LedgerAccount save(LedgerAccount ledgerAccount);

    Optional<LedgerAccount> findById(UUID tenantId, UUID id);
}
