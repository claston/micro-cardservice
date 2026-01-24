package com.sistema.ledger.api.dto;

import java.util.UUID;

public class CreateAccountResponse {
    private UUID accountId;

    public CreateAccountResponse() {
    }

    public CreateAccountResponse(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
}
