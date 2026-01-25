package com.sistema.ledger.application;

import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CreateAccountUseCase {
    private final LedgerAccountRepository ledgerAccountRepository;

    public CreateAccountUseCase(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    @Transactional
    public LedgerAccount execute(CreateAccountCommand command) {
        Objects.requireNonNull(command, "command");

        LedgerAccount ledgerAccount = new LedgerAccount(
                UUID.randomUUID(),
                command.getTenantId(),
                command.getName(),
                command.getType(),
                command.getCurrency(),
                command.isAllowNegative(),
                AccountStatus.ACTIVE,
                Instant.now()
        );

        return ledgerAccountRepository.save(ledgerAccount);
    }
}
