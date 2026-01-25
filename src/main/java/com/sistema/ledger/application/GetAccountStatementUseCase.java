package com.sistema.ledger.application;

import com.sistema.ledger.application.model.StatementPage;
import com.sistema.ledger.domain.repository.EntryRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class GetAccountStatementUseCase {
    private final EntryRepository entryRepository;

    public GetAccountStatementUseCase(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public StatementPage execute(UUID tenantId, UUID accountId, Instant from, Instant to, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        return entryRepository.getStatement(tenantId, accountId, from, to, safePage, safeSize);
    }
}
