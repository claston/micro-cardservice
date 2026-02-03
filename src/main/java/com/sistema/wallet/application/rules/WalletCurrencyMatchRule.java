package com.sistema.wallet.application.rules;

import com.sistema.wallet.domain.validation.WalletTransferPolicy;

import java.util.Optional;

public class WalletCurrencyMatchRule implements WalletTransferRule {
    @Override
    public Optional<WalletRuleViolation> validate(WalletTransferRuleContext context) {
        try {
            new WalletTransferPolicy().validateCurrencyMatch(
                    context.getFromAccount().getCurrency(),
                    context.getToAccount().getCurrency(),
                    context.getCommand().getCurrency()
            );
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.of(new WalletRuleViolation(ex));
        }
    }
}
