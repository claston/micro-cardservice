package com.sistema.ledger.application.rules;

import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.validation.LedgerPostingPolicy;

import java.util.Optional;

public class CurrencyMatchRule implements LedgerPostingRule {
    @Override
    public Optional<LedgerRuleViolation> validate(LedgerPostingRuleContext context) {
        if (context.getCommand().getEntries() == null) {
            return Optional.empty();
        }
        LedgerPostingPolicy policy = new LedgerPostingPolicy();
        for (PostingEntryCommand entryCommand : context.getCommand().getEntries()) {
            LedgerAccount account = context.getAccountsById().get(entryCommand.getLedgerAccountId());
            if (account == null) {
                continue;
            }
            try {
                policy.validateCurrencyMatch(
                        account.getCurrency(),
                        entryCommand.getCurrency(),
                        account.getId()
                );
            } catch (IllegalArgumentException ex) {
                return Optional.of(new LedgerRuleViolation(ex.getMessage()));
            }
        }
        return Optional.empty();
    }
}
