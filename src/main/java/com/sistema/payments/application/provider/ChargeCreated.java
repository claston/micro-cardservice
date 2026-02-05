package com.sistema.payments.application.provider;

import java.time.Instant;

public class ChargeCreated {
    private final String externalPaymentId;
    private final String txid;
    private final String qrCode;
    private final String copyPaste;
    private final Instant expiresAt;

    public ChargeCreated(String externalPaymentId,
                         String txid,
                         String qrCode,
                         String copyPaste,
                         Instant expiresAt) {
        this.externalPaymentId = externalPaymentId;
        this.txid = txid;
        this.qrCode = qrCode;
        this.copyPaste = copyPaste;
        this.expiresAt = expiresAt;
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
