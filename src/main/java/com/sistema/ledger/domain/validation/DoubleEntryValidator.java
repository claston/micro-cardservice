package com.sistema.ledger.domain.validation;

import com.sistema.ledger.domain.model.Entry;

import java.util.List;

public final class DoubleEntryValidator {
    private DoubleEntryValidator() {
    }

    public static void validate(List<Entry> entries) {
        new LedgerPostingPolicy().validateDoubleEntry(entries);
    }
}
