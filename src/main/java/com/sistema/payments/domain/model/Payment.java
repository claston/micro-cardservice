package com.sistema.payments.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Payment {
    private final UUID id;
    private final UUID tenantId;
    private final PaymentType type;
    private PaymentStatus status;
    private final long amountMinor;
    private final String currency;
    private final String referenceType;
    private final String referenceId;
    private final String idempotencyKey;
    private String externalProvider;
    private String externalPaymentId;
    private String externalTxid;
    private String qrCode;
    private String copyPaste;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant confirmedAt;
    private String failureReason;
    private UUID walletFromAccountId;
    private UUID walletToAccountId;
    private UUID ledgerTransactionId;

    public Payment(UUID id,
                   UUID tenantId,
                   PaymentType type,
                   PaymentStatus status,
                   long amountMinor,
                   String currency,
                   String referenceType,
                   String referenceId,
                   String idempotencyKey,
                   String externalProvider,
                   String externalPaymentId,
                   String externalTxid,
                   String qrCode,
                   String copyPaste,
                   Instant expiresAt,
                   Instant createdAt,
                   Instant updatedAt,
                   Instant confirmedAt,
                   String failureReason,
                   UUID walletFromAccountId,
                   UUID walletToAccountId,
                   UUID ledgerTransactionId) {
        this.id = id;
        this.tenantId = tenantId;
        this.type = type;
        this.status = status;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.idempotencyKey = idempotencyKey;
        this.externalProvider = externalProvider;
        this.externalPaymentId = externalPaymentId;
        this.externalTxid = externalTxid;
        this.qrCode = qrCode;
        this.copyPaste = copyPaste;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.confirmedAt = confirmedAt;
        this.failureReason = failureReason;
        this.walletFromAccountId = walletFromAccountId;
        this.walletToAccountId = walletToAccountId;
        this.ledgerTransactionId = ledgerTransactionId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public PaymentType getType() {
        return type;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getExternalProvider() {
        return externalProvider;
    }

    public void setExternalProvider(String externalProvider) {
        this.externalProvider = externalProvider;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    public void setExternalPaymentId(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    public String getExternalTxid() {
        return externalTxid;
    }

    public void setExternalTxid(String externalTxid) {
        this.externalTxid = externalTxid;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getCopyPaste() {
        return copyPaste;
    }

    public void setCopyPaste(String copyPaste) {
        this.copyPaste = copyPaste;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public UUID getWalletFromAccountId() {
        return walletFromAccountId;
    }

    public void setWalletFromAccountId(UUID walletFromAccountId) {
        this.walletFromAccountId = walletFromAccountId;
    }

    public UUID getWalletToAccountId() {
        return walletToAccountId;
    }

    public void setWalletToAccountId(UUID walletToAccountId) {
        this.walletToAccountId = walletToAccountId;
    }

    public UUID getLedgerTransactionId() {
        return ledgerTransactionId;
    }

    public void setLedgerTransactionId(UUID ledgerTransactionId) {
        this.ledgerTransactionId = ledgerTransactionId;
    }
}
