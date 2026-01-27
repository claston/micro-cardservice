package com.sistema.wallet.application.exception;

import com.sistema.wallet.api.error.WalletErrorCodes;

import java.util.UUID;

public class WalletAccountNotFoundException extends WalletException {
    public WalletAccountNotFoundException(UUID accountId) {
        super("wallet account not found: " + accountId, 404, WalletErrorCodes.WALLET_ACCOUNT_NOT_FOUND);
    }
}
