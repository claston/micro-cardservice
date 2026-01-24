package com.sistema.ledger.application;

import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.repository.AccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CreateAccountUseCase {
    private final AccountRepository accountRepository;

    public CreateAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account execute(CreateAccountCommand command) {
        Objects.requireNonNull(command, "command");

        Account account = new Account(
                UUID.randomUUID(),
                command.getName(),
                command.getType(),
                command.getCurrency(),
                command.isAllowNegative(),
                AccountStatus.ACTIVE,
                Instant.now()
        );

        return accountRepository.save(account);
    }
}
