package com.sistema.wallet.application.rules;

import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.exception.WalletAccountNotFoundException;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class WalletTransferRuleContextFactory {
    private final WalletAccountRepository walletAccountRepository;

    public WalletTransferRuleContextFactory(WalletAccountRepository walletAccountRepository) {
        this.walletAccountRepository = walletAccountRepository;
    }

    public WalletTransferRuleContext build(UUID tenantId, TransferBetweenWalletAccountsCommand command) {
        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId");
        }
        if (command == null) {
            throw new IllegalArgumentException("command");
        }

        WalletAccount fromAccount = walletAccountRepository.findById(tenantId, command.getFromAccountId())
                .orElseThrow(() -> new WalletAccountNotFoundException(command.getFromAccountId()));
        WalletAccount toAccount = walletAccountRepository.findById(tenantId, command.getToAccountId())
                .orElseThrow(() -> new WalletAccountNotFoundException(command.getToAccountId()));

        return new WalletTransferRuleContext(tenantId, command, fromAccount, toAccount);
    }
}
