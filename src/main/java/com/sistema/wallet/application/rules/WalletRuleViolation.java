package com.sistema.wallet.application.rules;

public class WalletRuleViolation {
    private final RuntimeException exception;

    public WalletRuleViolation(RuntimeException exception) {
        this.exception = exception;
    }

    public RuntimeException getException() {
        return exception;
    }
}
