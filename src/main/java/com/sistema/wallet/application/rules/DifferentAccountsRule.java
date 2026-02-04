package com.sistema.wallet.application.rules;

import com.sistema.wallet.domain.validation.WalletTransferPolicy;

import java.util.Optional;

public class DifferentAccountsRule implements WalletTransferRule {
    @Override
    public Optional<WalletRuleViolation> validate(WalletTransferRuleContext context) {
        try {
            new WalletTransferPolicy().validateDifferentAccounts(
                    context.getFromAccount().getId(),
                    context.getToAccount().getId()
            );
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.of(new WalletRuleViolation(ex));
        }
    }
}
