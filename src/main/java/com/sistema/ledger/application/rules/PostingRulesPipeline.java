package com.sistema.ledger.application.rules;

import com.sistema.ledger.domain.repository.EntryRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PostingRulesPipeline {
    private final List<LedgerPostingRule> rules;

    public PostingRulesPipeline(EntryRepository entryRepository) {
        this.rules = List.of(
                new EntriesCountRule(),
                new DistinctAccountsRule(),
                new AccountStatusActiveRule(),
                new CrossTenantRule(),
                new CurrencyMatchRule(),
                new DoubleEntryRule(),
                new NegativeBalanceRule(entryRepository)
        );
    }

    public void validate(LedgerPostingRuleContext context) {
        for (LedgerPostingRule rule : rules) {
            var violation = rule.validate(context);
            if (violation.isPresent()) {
                throw new IllegalArgumentException(violation.get().getMessage());
            }
        }
    }
}
