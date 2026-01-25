package com.sistema.wallet.application.command;

import java.util.UUID;

public class TransferBetweenWalletAccountsCommand {
    private final String idempotencyKey;
    private final UUID fromAccountId;
    private final UUID toAccountId;
    private final long amountMinor;
    private final String currency;
    private final String description;

    public TransferBetweenWalletAccountsCommand(String idempotencyKey,
                                                UUID fromAccountId,
                                                UUID toAccountId,
                                                long amountMinor,
                                                String currency,
                                                String description) {
        this.idempotencyKey = idempotencyKey;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.description = description;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }
}
