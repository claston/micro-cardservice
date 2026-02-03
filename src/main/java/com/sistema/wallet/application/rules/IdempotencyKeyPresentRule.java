package com.sistema.wallet.application.rules;

import com.sistema.wallet.domain.validation.WalletTransferPolicy;

import java.util.Optional;

public class IdempotencyKeyPresentRule implements WalletTransferRule {
    @Override
    public Optional<WalletRuleViolation> validate(WalletTransferRuleContext context) {
        try {
            new WalletTransferPolicy().validateIdempotencyKey(context.getCommand().getIdempotencyKey());
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.of(new WalletRuleViolation(ex));
        }
    }
}
