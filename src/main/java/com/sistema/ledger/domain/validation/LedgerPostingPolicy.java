package com.sistema.ledger.domain.validation;

import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.LedgerAccount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LedgerPostingPolicy {
    public void validateEntriesCount(int entriesCount) {
        if (entriesCount < 2) {
            throw new IllegalArgumentException("entries must have at least 2 items");
        }
    }

    public void validateAccountActive(LedgerAccount account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("account not active: " + account.getId());
        }
    }

    public void validateCrossTenant(UUID tenantId, LedgerAccount account) {
        if (!account.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("cross-tenant operation is not allowed");
        }
    }

    public void validateCurrencyMatch(String accountCurrency, String requestedCurrency, UUID accountId) {
        if (requestedCurrency == null) {
            return;
        }
        if (!accountCurrency.equals(requestedCurrency)) {
            throw new IllegalArgumentException("currency mismatch for account " + accountId);
        }
    }

    public void validateDoubleEntry(List<Entry> entries) {
        validateEntriesCount(entries == null ? 0 : entries.size());

        Map<String, Long> debitByCurrency = new HashMap<>();
        Map<String, Long> creditByCurrency = new HashMap<>();

        for (Entry entry : entries) {
            String currency = entry.getMoney().getCurrency();
            long amount = entry.getMoney().getAmountMinor();
            if (entry.getDirection() == EntryDirection.DEBIT) {
                debitByCurrency.merge(currency, amount, Long::sum);
            } else {
                creditByCurrency.merge(currency, amount, Long::sum);
            }
        }

        for (Map.Entry<String, Long> debit : debitByCurrency.entrySet()) {
            long credits = creditByCurrency.getOrDefault(debit.getKey(), 0L);
            if (!debit.getValue().equals(credits)) {
                throw new IllegalArgumentException("double-entry validation failed for currency " + debit.getKey());
            }
        }

        for (Map.Entry<String, Long> credit : creditByCurrency.entrySet()) {
            long debits = debitByCurrency.getOrDefault(credit.getKey(), 0L);
            if (!credit.getValue().equals(debits)) {
                throw new IllegalArgumentException("double-entry validation failed for currency " + credit.getKey());
            }
        }
    }
}
