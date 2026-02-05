package com.sistema.payments.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "amount_minor")
    private long amountMinor;

    @Column(name = "currency")
    private String currency;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "external_provider")
    private String externalProvider;

    @Column(name = "external_payment_id")
    private String externalPaymentId;

    @Column(name = "external_txid")
    private String externalTxid;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "copy_paste")
    private String copyPaste;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "wallet_from_account_id")
    private UUID walletFromAccountId;

    @Column(name = "wallet_to_account_id")
    private UUID walletToAccountId;

    @Column(name = "ledger_transaction_id")
    private UUID ledgerTransactionId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public void setAmountMinor(long amountMinor) {
        this.amountMinor = amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
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
