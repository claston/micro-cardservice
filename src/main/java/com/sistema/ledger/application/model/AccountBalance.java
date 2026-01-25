package com.sistema.ledger.application.model;

import java.util.UUID;

public class AccountBalance {
    private final UUID ledgerAccountId;
    private final long balanceMinor;
    private final String currency;

    public AccountBalance(UUID ledgerAccountId, long balanceMinor, String currency) {
        this.ledgerAccountId = ledgerAccountId;
        this.balanceMinor = balanceMinor;
        this.currency = currency;
    }

    public UUID getLedgerAccountId() {
        return ledgerAccountId;
    }

    public long getBalanceMinor() {
        return balanceMinor;
    }

    public String getCurrency() {
        return currency;
    }
}
