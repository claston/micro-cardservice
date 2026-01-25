package com.sistema.ledger.application;

import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
import com.sistema.ledger.domain.repository.EntryRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class GetAccountBalanceUseCase {
    private final LedgerAccountRepository ledgerAccountRepository;
    private final EntryRepository entryRepository;

    public GetAccountBalanceUseCase(LedgerAccountRepository ledgerAccountRepository, EntryRepository entryRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
        this.entryRepository = entryRepository;
    }

    public AccountBalance execute(UUID tenantId, UUID ledgerAccountId) {
        LedgerAccount ledgerAccount = ledgerAccountRepository.findById(tenantId, ledgerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("account not found: " + ledgerAccountId));
        long balance = entryRepository.getBalanceMinor(tenantId, ledgerAccountId);
        return new AccountBalance(ledgerAccountId, balance, ledgerAccount.getCurrency());
    }
}
