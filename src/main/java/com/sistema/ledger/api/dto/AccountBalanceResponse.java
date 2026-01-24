package com.sistema.ledger.api.dto;

import java.util.UUID;

public class AccountBalanceResponse {
    private UUID accountId;
    private long balanceMinor;
    private String currency;

    public AccountBalanceResponse() {
    }

    public AccountBalanceResponse(UUID accountId, long balanceMinor, String currency) {
        this.accountId = accountId;
        this.balanceMinor = balanceMinor;
        this.currency = currency;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public long getBalanceMinor() {
        return balanceMinor;
    }

    public void setBalanceMinor(long balanceMinor) {
        this.balanceMinor = balanceMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
