package com.sistema.wallet.api.error;

public final class WalletErrorCodes {
    public static final String WALLET_VALIDATION_ERROR = "WALLET_VALIDATION_ERROR";
    public static final String WALLET_UNAUTHORIZED = "WALLET_UNAUTHORIZED";
    public static final String WALLET_ACCOUNT_NOT_FOUND = "WALLET_ACCOUNT_NOT_FOUND";
    public static final String WALLET_ACCOUNT_ALREADY_EXISTS = "WALLET_ACCOUNT_ALREADY_EXISTS";
    public static final String WALLET_OWNER_NOT_FOUND = "WALLET_OWNER_NOT_FOUND";
    public static final String WALLET_INSUFFICIENT_BALANCE = "WALLET_INSUFFICIENT_BALANCE";
    public static final String WALLET_IDEMPOTENCY_CONFLICT = "WALLET_IDEMPOTENCY_CONFLICT";
    public static final String WALLET_INTERNAL_ERROR = "WALLET_INTERNAL_ERROR";

    private WalletErrorCodes() {
    }
}
