package com.sistema.ledger.application.command;

import com.sistema.ledger.domain.model.AccountType;

public class CreateAccountCommand {
    private final String name;
    private final AccountType type;
    private final String currency;
    private final boolean allowNegative;

    public CreateAccountCommand(String name, AccountType type, String currency, boolean allowNegative) {
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.allowNegative = allowNegative;
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
