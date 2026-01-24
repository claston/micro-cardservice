package com.sistema.ledger.domain.model;

import java.util.Objects;

public class IdempotencyKey {
    private final String value;

    public IdempotencyKey(String value) {
        this.value = Objects.requireNonNull(value, "idempotencyKey");
        if (this.value.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
    }

    public String getValue() {
        return value;
    }
}
