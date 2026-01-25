package com.sistema.wallet.application.model;

import java.util.UUID;

public class WalletBalance {
    private final UUID accountId;
    private final long balanceMinor;
    private final String currency;

    public WalletBalance(UUID accountId, long balanceMinor, String currency) {
        this.accountId = accountId;
        this.balanceMinor = balanceMinor;
        this.currency = currency;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public long getBalanceMinor() {
        return balanceMinor;
    }

    public String getCurrency() {
        return currency;
    }
}
