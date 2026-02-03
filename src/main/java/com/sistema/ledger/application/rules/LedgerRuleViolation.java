package com.sistema.ledger.application.rules;

public class LedgerRuleViolation {
    private final String message;

    public LedgerRuleViolation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
