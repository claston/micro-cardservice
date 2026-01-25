package com.sistema.wallet.application.command;

import com.sistema.wallet.domain.model.WalletOwnerType;

public class CreateWalletAccountCommand {
    private final WalletOwnerType ownerType;
    private final String ownerId;
    private final String currency;
    private final String label;

    public CreateWalletAccountCommand(WalletOwnerType ownerType, String ownerId, String currency, String label) {
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.currency = currency;
        this.label = label;
    }

    public WalletOwnerType getOwnerType() {
        return ownerType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLabel() {
        return label;
    }
}
