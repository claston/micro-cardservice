package com.sistema.ledger.application.rules;

import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.validation.LedgerPostingPolicy;

import java.util.Optional;

public class AccountStatusActiveRule implements LedgerPostingRule {
    @Override
    public Optional<LedgerRuleViolation> validate(LedgerPostingRuleContext context) {
        LedgerPostingPolicy policy = new LedgerPostingPolicy();
        for (LedgerAccount account : context.getAccountsById().values()) {
            try {
                policy.validateAccountActive(account);
            } catch (IllegalArgumentException ex) {
                return Optional.of(new LedgerRuleViolation(ex.getMessage()));
            }
        }
        return Optional.empty();
    }
}
