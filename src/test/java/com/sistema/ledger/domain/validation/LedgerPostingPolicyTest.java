package com.sistema.ledger.domain.validation;

import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LedgerPostingPolicyTest {

    @Test
    void shouldValidateWhenDebitsEqualCreditsSameCurrency() {
        List<Entry> entries = List.of(
                entry(EntryDirection.DEBIT, 1000, "BRL"),
                entry(EntryDirection.CREDIT, 1000, "BRL")
        );

        assertDoesNotThrow(() -> new LedgerPostingPolicy().validateDoubleEntry(entries));
    }

    @Test
    void shouldValidateWhenDebitsEqualCreditsMultipleCurrencies() {
        List<Entry> entries = List.of(
                entry(EntryDirection.DEBIT, 1000, "BRL"),
                entry(EntryDirection.CREDIT, 1000, "BRL"),
                entry(EntryDirection.DEBIT, 500, "USD"),
                entry(EntryDirection.CREDIT, 500, "USD")
        );

        assertDoesNotThrow(() -> new LedgerPostingPolicy().validateDoubleEntry(entries));
    }

    @Test
    void shouldFailWhenDebitsDoNotMatchCredits() {
        List<Entry> entries = List.of(
                entry(EntryDirection.DEBIT, 1000, "BRL"),
                entry(EntryDirection.CREDIT, 900, "BRL")
        );

        assertThrows(IllegalArgumentException.class, () -> new LedgerPostingPolicy().validateDoubleEntry(entries));
    }

    @Test
    void shouldFailWhenMissingCreditForCurrency() {
        List<Entry> entries = List.of(
                entry(EntryDirection.DEBIT, 1000, "BRL"),
                entry(EntryDirection.CREDIT, 1000, "BRL"),
                entry(EntryDirection.DEBIT, 200, "USD")
        );

        assertThrows(IllegalArgumentException.class, () -> new LedgerPostingPolicy().validateDoubleEntry(entries));
    }

    private Entry entry(EntryDirection direction, long amountMinor, String currency) {
        return new Entry(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                direction,
                new Money(amountMinor, currency),
                Instant.now(),
                Instant.now()
        );
    }
}
