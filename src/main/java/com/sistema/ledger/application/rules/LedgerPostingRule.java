package com.sistema.ledger.application.rules;

import java.util.Optional;

public interface LedgerPostingRule {
    Optional<LedgerRuleViolation> validate(LedgerPostingRuleContext context);
}
