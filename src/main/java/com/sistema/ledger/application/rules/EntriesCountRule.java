package com.sistema.ledger.application.rules;

import com.sistema.ledger.domain.validation.LedgerPostingPolicy;

import java.util.Optional;

public class EntriesCountRule implements LedgerPostingRule {
    @Override
    public Optional<LedgerRuleViolation> validate(LedgerPostingRuleContext context) {
        try {
            int count = context.getCommand().getEntries() == null ? 0 : context.getCommand().getEntries().size();
            new LedgerPostingPolicy().validateEntriesCount(count);
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.of(new LedgerRuleViolation(ex.getMessage()));
        }
    }
}
