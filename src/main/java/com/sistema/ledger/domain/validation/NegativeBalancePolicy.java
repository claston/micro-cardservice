package com.sistema.ledger.domain.validation;

public final class NegativeBalancePolicy {
    private NegativeBalancePolicy() {
    }

    public static void check(boolean allowNegative, long resultingBalanceMinor) {
        if (!allowNegative && resultingBalanceMinor < 0) {
            throw new IllegalArgumentException("negative balance is not allowed");
        }
    }
}
