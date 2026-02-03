package com.sistema.ledger.application.rules;

import com.sistema.ledger.domain.validation.LedgerPostingPolicy;

import java.util.Optional;

public class DoubleEntryRule implements LedgerPostingRule {
    @Override
    public Optional<LedgerRuleViolation> validate(LedgerPostingRuleContext context) {
        try {
            new LedgerPostingPolicy().validateDoubleEntry(context.getEntries());
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.of(new LedgerRuleViolation(ex.getMessage()));
        }
    }
}
