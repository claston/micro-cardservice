package com.sistema.wallet.application.exception;

import com.sistema.wallet.api.error.WalletErrorCodes;

public class WalletInsufficientBalanceException extends WalletException {
    public WalletInsufficientBalanceException(String message) {
        super(message, 409, WalletErrorCodes.WALLET_INSUFFICIENT_BALANCE);
    }
}
