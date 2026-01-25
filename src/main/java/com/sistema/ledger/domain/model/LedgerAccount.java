package com.sistema.ledger.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class LedgerAccount {
    private final UUID id;
    private final UUID tenantId;
    private final String name;
    private final AccountType type;
    private final String currency;
    private final boolean allowNegative;
    private final AccountStatus status;
    private final Instant createdAt;

    public LedgerAccount(UUID id,
                         UUID tenantId,
                         String name,
                         AccountType type,
                         String currency,
                         boolean allowNegative,
                         AccountStatus status,
                         Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.name = Objects.requireNonNull(name, "name");
        if (this.name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.type = Objects.requireNonNull(type, "type");
        this.currency = Objects.requireNonNull(currency, "currency");
        if (this.currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
        this.allowNegative = allowNegative;
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
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

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
