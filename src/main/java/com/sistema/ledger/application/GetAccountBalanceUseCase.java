package com.sistema.ledger.application;

import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.repository.AccountRepository;
import com.sistema.ledger.domain.repository.EntryRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class GetAccountBalanceUseCase {
    private final AccountRepository accountRepository;
    private final EntryRepository entryRepository;

    public GetAccountBalanceUseCase(AccountRepository accountRepository, EntryRepository entryRepository) {
        this.accountRepository = accountRepository;
        this.entryRepository = entryRepository;
    }

    public AccountBalance execute(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("account not found: " + accountId));
        long balance = entryRepository.getBalanceMinor(accountId);
        return new AccountBalance(accountId, balance, account.getCurrency());
    }
}
