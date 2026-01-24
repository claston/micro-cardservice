package com.sistema.ledger.domain.model;

import java.util.Objects;

public class Money {
    private final long amountMinor;
    private final String currency;

    public Money(long amountMinor, String currency) {
        if (amountMinor < 0) {
            throw new IllegalArgumentException("amountMinor must be >= 0");
        }
        this.amountMinor = amountMinor;
        this.currency = Objects.requireNonNull(currency, "currency");
        if (this.currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public String getCurrency() {
        return currency;
    }
}
