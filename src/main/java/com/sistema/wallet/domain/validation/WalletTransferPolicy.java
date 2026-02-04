package com.sistema.wallet.domain.validation;

import com.sistema.wallet.domain.model.WalletOwnerType;

import java.util.UUID;

public class WalletTransferPolicy {
    public void validateAmountPositive(long amountMinor) {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
    }

    public void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey is required");
        }
    }

    public void validateCurrencyMatch(String fromCurrency, String toCurrency, String requestedCurrency) {
        if (!fromCurrency.equals(requestedCurrency) || !toCurrency.equals(requestedCurrency)) {
            throw new IllegalArgumentException("currency mismatch");
        }
    }

    public void validateSufficientBalance(WalletOwnerType ownerType, long balanceMinor, long amountMinor) {
        if (ownerType == WalletOwnerType.FUNDING) {
            return;
        }
        if (balanceMinor < amountMinor) {
            throw new IllegalArgumentException("insufficient balance");
        }
    }

    public void validateDifferentAccounts(UUID fromAccountId, UUID toAccountId) {
        if (fromAccountId != null && fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("fromAccountId must be different from toAccountId");
        }
    }
}
