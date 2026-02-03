package com.sistema.wallet.application.rules;

import java.util.Optional;

public interface WalletTransferRule {
    Optional<WalletRuleViolation> validate(WalletTransferRuleContext context);
}
