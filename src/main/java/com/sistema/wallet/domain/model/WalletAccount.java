package com.sistema.wallet.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WalletAccount {
    private final UUID id;
    private final UUID tenantId;
    private final WalletOwnerType ownerType;
    private final String ownerId;
    private final String currency;
    private final WalletAccountStatus status;
    private final String label;
    private final UUID ledgerAccountId;
    private final Instant createdAt;

    public WalletAccount(UUID id,
                         UUID tenantId,
                         WalletOwnerType ownerType,
                         String ownerId,
                         String currency,
                         WalletAccountStatus status,
                         String label,
                         UUID ledgerAccountId,
                         Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.ownerType = Objects.requireNonNull(ownerType, "ownerType");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
        if (this.ownerId.isBlank()) {
            throw new IllegalArgumentException("ownerId must not be blank");
        }
        this.currency = Objects.requireNonNull(currency, "currency");
        if (this.currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
        this.status = Objects.requireNonNull(status, "status");
        this.label = label;
        this.ledgerAccountId = Objects.requireNonNull(ledgerAccountId, "ledgerAccountId");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public WalletOwnerType getOwnerType() {
        return ownerType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getCurrency() {
        return currency;
    }

    public WalletAccountStatus getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

    public UUID getLedgerAccountId() {
        return ledgerAccountId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
