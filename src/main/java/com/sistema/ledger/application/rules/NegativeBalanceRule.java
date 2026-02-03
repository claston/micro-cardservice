package com.sistema.ledger.application.rules;

import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.repository.EntryRepository;
import com.sistema.ledger.domain.validation.NegativeBalancePolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NegativeBalanceRule implements LedgerPostingRule {
    private final EntryRepository entryRepository;

    public NegativeBalanceRule(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @Override
    public Optional<LedgerRuleViolation> validate(LedgerPostingRuleContext context) {
        Map<UUID, Long> deltaByAccount = new HashMap<>();
        for (Entry entry : context.getEntries()) {
            long delta = entry.getDirection() == EntryDirection.CREDIT
                    ? entry.getMoney().getAmountMinor()
                    : -entry.getMoney().getAmountMinor();
            deltaByAccount.merge(entry.getLedgerAccountId(), delta, Long::sum);
        }

        for (Map.Entry<UUID, Long> delta : deltaByAccount.entrySet()) {
            LedgerAccount account = context.getAccountsById().get(delta.getKey());
            if (account == null) {
                continue;
            }
            long currentBalance = entryRepository.getBalanceMinor(context.getCommand().getTenantId(), account.getId());
            long resultingBalance = currentBalance + delta.getValue();
            try {
                NegativeBalancePolicy.check(account.isAllowNegative(), resultingBalance);
            } catch (IllegalArgumentException ex) {
                return Optional.of(new LedgerRuleViolation(ex.getMessage()));
            }
        }

        return Optional.empty();
    }
}
