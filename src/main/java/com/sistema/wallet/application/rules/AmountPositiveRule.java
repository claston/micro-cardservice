package com.sistema.wallet.application.rules;

import com.sistema.wallet.domain.validation.WalletTransferPolicy;

import java.util.Optional;

public class AmountPositiveRule implements WalletTransferRule {
    @Override
    public Optional<WalletRuleViolation> validate(WalletTransferRuleContext context) {
        try {
            new WalletTransferPolicy().validateAmountPositive(context.getCommand().getAmountMinor());
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.of(new WalletRuleViolation(ex));
        }
    }
}
