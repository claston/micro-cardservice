package com.sistema.wallet.application.model;

import java.util.UUID;

public class WalletTransferResult {
    private final UUID transactionId;
    private final String status;

    public WalletTransferResult(UUID transactionId, String status) {
        this.transactionId = transactionId;
        this.status = status;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }
}
