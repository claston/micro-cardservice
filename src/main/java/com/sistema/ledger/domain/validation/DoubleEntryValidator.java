package com.sistema.ledger.domain.validation;

import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DoubleEntryValidator {
    private DoubleEntryValidator() {
    }

    public static void validate(List<Entry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("entries must have at least 2 items");
        }

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
            if (debit.getValue() != credits) {
                throw new IllegalArgumentException("double-entry validation failed for currency " + debit.getKey());
            }
        }

        for (Map.Entry<String, Long> credit : creditByCurrency.entrySet()) {
            long debits = debitByCurrency.getOrDefault(credit.getKey(), 0L);
            if (credit.getValue() != debits) {
                throw new IllegalArgumentException("double-entry validation failed for currency " + credit.getKey());
            }
        }
    }
}
