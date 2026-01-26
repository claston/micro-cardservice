package com.sistema.wallet.application.exception;

import com.sistema.wallet.api.error.WalletErrorCodes;

public class WalletAccountAlreadyExistsException extends WalletException {
    public WalletAccountAlreadyExistsException() {
        super("wallet account already exists", 409, WalletErrorCodes.WALLET_ACCOUNT_ALREADY_EXISTS);
    }
}
