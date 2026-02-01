package com.sistema.wallet.application.exception;

import com.sistema.wallet.api.error.WalletErrorCodes;

public class WalletOwnerNotFoundException extends WalletException {
    public WalletOwnerNotFoundException(String ownerId) {
        super("owner not found: " + ownerId, 404, WalletErrorCodes.WALLET_OWNER_NOT_FOUND);
    }
}
