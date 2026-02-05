package com.sistema.payments.application.model;

import java.time.Instant;
import java.util.UUID;

public class PixChargeResult {
    private final UUID paymentId;
    private final String status;
    private final String externalPaymentId;
    private final String txid;
    private final String qrCode;
    private final String copyPaste;
    private final Instant expiresAt;

    public PixChargeResult(UUID paymentId,
                           String status,
                           String externalPaymentId,
                           String txid,
                           String qrCode,
                           String copyPaste,
                           Instant expiresAt) {
        this.paymentId = paymentId;
        this.status = status;
        this.externalPaymentId = externalPaymentId;
        this.txid = txid;
        this.qrCode = qrCode;
        this.copyPaste = copyPaste;
        this.expiresAt = expiresAt;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    public String getTxid() {
        return txid;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getCopyPaste() {
        return copyPaste;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
