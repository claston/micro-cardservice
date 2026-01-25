package com.sistema.ledger.application.command;

import com.sistema.ledger.domain.model.AccountType;

public class CreateAccountCommand {
    private final java.util.UUID tenantId;
    private final String name;
    private final AccountType type;
    private final String currency;
    private final boolean allowNegative;

    public CreateAccountCommand(java.util.UUID tenantId,
                                String name,
                                AccountType type,
                                String currency,
                                boolean allowNegative) {
        this.tenantId = tenantId;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.allowNegative = allowNegative;
    }

    public java.util.UUID getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isAllowNegative() {
        return allowNegative;
    }
}
