package com.sistema.payments.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class CreatePixPayoutRequest {
    @NotBlank(message = "referenceType is required")
    private String referenceType;

    @NotBlank(message = "referenceId is required")
    private String referenceId;

    @Positive(message = "amountMinor must be greater than zero")
    private long amountMinor;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code")
    private String currency;

    @NotBlank(message = "pixKey is required")
    private String pixKey;

    @NotBlank(message = "debitFromWalletAccountId is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "debitFromWalletAccountId must be a valid UUID")
    private String debitFromWalletAccountId;

    private String description;

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public void setAmountMinor(long amountMinor) {
        this.amountMinor = amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }

    public String getDebitFromWalletAccountId() {
        return debitFromWalletAccountId;
    }

    public void setDebitFromWalletAccountId(String debitFromWalletAccountId) {
        this.debitFromWalletAccountId = debitFromWalletAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
