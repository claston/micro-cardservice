package com.sistema.wallet.api.dto;

import com.sistema.common.api.validation.ValidEnum;
import com.sistema.wallet.domain.model.WalletOwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateWalletAccountRequest {
    @NotBlank(message = "ownerType is required")
    @ValidEnum(enumClass = WalletOwnerType.class, message = "ownerType must be one of: CUSTOMER, FUNDING")
    private String ownerType;

    @NotBlank(message = "ownerId is required")
    private String ownerId;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code")
    private String currency;

    private String label;

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
