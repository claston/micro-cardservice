package com.sistema.wallet.application.exception;

import com.sistema.wallet.api.error.WalletErrorCodes;

public class WalletUnauthorizedException extends WalletException {
    public WalletUnauthorizedException(String message) {
        super(message, 401, WalletErrorCodes.WALLET_UNAUTHORIZED);
    }
}
