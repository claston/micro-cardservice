package com.sistema.wallet.application.rules;

import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.domain.model.WalletAccount;

import java.util.UUID;

public class WalletTransferRuleContext {
    private final UUID tenantId;
    private final TransferBetweenWalletAccountsCommand command;
    private final WalletAccount fromAccount;
    private final WalletAccount toAccount;

    public WalletTransferRuleContext(UUID tenantId,
                                     TransferBetweenWalletAccountsCommand command,
                                     WalletAccount fromAccount,
                                     WalletAccount toAccount) {
        this.tenantId = tenantId;
        this.command = command;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public TransferBetweenWalletAccountsCommand getCommand() {
        return command;
    }

    public WalletAccount getFromAccount() {
        return fromAccount;
    }

    public WalletAccount getToAccount() {
        return toAccount;
    }
}
