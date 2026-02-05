package com.sistema.payments.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class CreatePixChargeRequest {
    @NotBlank(message = "referenceType is required")
    private String referenceType;

    @NotBlank(message = "referenceId is required")
    private String referenceId;

    @Positive(message = "amountMinor must be greater than zero")
    private long amountMinor;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code")
    private String currency;

    @Valid
    @NotNull(message = "payer is required")
    private PayerRequest payer;

    @NotBlank(message = "creditToWalletAccountId is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "creditToWalletAccountId must be a valid UUID")
    private String creditToWalletAccountId;

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

    public PayerRequest getPayer() {
        return payer;
    }

    public void setPayer(PayerRequest payer) {
        this.payer = payer;
    }

    public String getCreditToWalletAccountId() {
        return creditToWalletAccountId;
    }

    public void setCreditToWalletAccountId(String creditToWalletAccountId) {
        this.creditToWalletAccountId = creditToWalletAccountId;
    }
}
