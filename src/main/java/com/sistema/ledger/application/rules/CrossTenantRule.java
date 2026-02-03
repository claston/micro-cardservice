package com.sistema.ledger.application.rules;

import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.validation.LedgerPostingPolicy;

import java.util.Optional;
import java.util.UUID;

public class CrossTenantRule implements LedgerPostingRule {
    @Override
    public Optional<LedgerRuleViolation> validate(LedgerPostingRuleContext context) {
        LedgerPostingPolicy policy = new LedgerPostingPolicy();
        UUID tenantId = context.getCommand().getTenantId();
        for (LedgerAccount account : context.getAccountsById().values()) {
            try {
                policy.validateCrossTenant(tenantId, account);
            } catch (IllegalArgumentException ex) {
                return Optional.of(new LedgerRuleViolation(ex.getMessage()));
            }
        }
        return Optional.empty();
    }
}
