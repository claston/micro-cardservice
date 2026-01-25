package com.sistema.wallet.application;

import com.sistema.ledger.application.CreateAccountUseCase;
import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.wallet.application.command.CreateWalletAccountCommand;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CreateWalletAccountUseCase {
    private final WalletAccountRepository walletAccountRepository;
    private final CreateAccountUseCase createAccountUseCase;

    public CreateWalletAccountUseCase(WalletAccountRepository walletAccountRepository,
                                      CreateAccountUseCase createAccountUseCase) {
        this.walletAccountRepository = walletAccountRepository;
        this.createAccountUseCase = createAccountUseCase;
    }

    @Transactional
    public WalletAccount execute(UUID tenantId, CreateWalletAccountCommand command) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(command, "command");

        walletAccountRepository.findByOwner(
                tenantId,
                command.getOwnerType(),
                command.getOwnerId(),
                command.getCurrency()
        ).ifPresent(existing -> {
            throw new IllegalArgumentException("wallet account already exists");
        });

        String ledgerAccountName = "WalletAccount " + command.getOwnerType() + ":" + command.getOwnerId();
        boolean allowNegative = command.getOwnerType() == WalletOwnerType.INTERNAL;
        LedgerAccount ledgerAccount = createAccountUseCase.execute(
                new CreateAccountCommand(
                        tenantId,
                        ledgerAccountName,
                        AccountType.LIABILITY,
                        command.getCurrency(),
                        allowNegative
                )
        );

        WalletAccount walletAccount = new WalletAccount(
                UUID.randomUUID(),
                tenantId,
                command.getOwnerType(),
                command.getOwnerId(),
                command.getCurrency(),
                WalletAccountStatus.ACTIVE,
                command.getLabel(),
                ledgerAccount.getId(),
                Instant.now()
        );

        return walletAccountRepository.save(walletAccount);
    }
}
