package com.sistema.wallet.application.exception;

import com.sistema.wallet.api.error.WalletErrorCodes;

import java.util.Map;
import java.util.UUID;

public class WalletIdempotencyConflictException extends WalletException {
    public WalletIdempotencyConflictException(String idempotencyKey, UUID transactionId) {
        super("idempotencyKey already used for this tenant", 409,
                WalletErrorCodes.WALLET_IDEMPOTENCY_CONFLICT,
                Map.of(
                        "idempotencyKey", idempotencyKey,
                        "transactionId", transactionId.toString()
                ));
    }
}
