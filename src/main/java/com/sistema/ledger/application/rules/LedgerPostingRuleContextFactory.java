package com.sistema.ledger.application.rules;

import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.model.Money;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class LedgerPostingRuleContextFactory {
    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerPostingRuleContextFactory(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    public LedgerPostingRuleContext build(PostLedgerTransactionCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command");
        }

        Map<UUID, LedgerAccount> accountsById = loadAccounts(command);
        Instant occurredAt = command.getOccurredAt() != null ? command.getOccurredAt() : Instant.now();
        Instant createdAt = Instant.now();

        List<Entry> entries = new ArrayList<>();
        if (command.getEntries() != null) {
            for (PostingEntryCommand entryCommand : command.getEntries()) {
                LedgerAccount ledgerAccount = accountsById.get(entryCommand.getLedgerAccountId());
                String currency = entryCommand.getCurrency() == null
                        ? ledgerAccount.getCurrency()
                        : entryCommand.getCurrency();
                Money money = new Money(entryCommand.getAmountMinor(), currency);
                Entry entry = new Entry(
                        UUID.randomUUID(),
                        command.getTenantId(),
                        UUID.randomUUID(),
                        ledgerAccount.getId(),
                        entryCommand.getDirection(),
                        money,
                        occurredAt,
                        createdAt
                );
                entries.add(entry);
            }
        }

        return new LedgerPostingRuleContext(command, accountsById, entries, occurredAt, createdAt);
    }

    private Map<UUID, LedgerAccount> loadAccounts(PostLedgerTransactionCommand command) {
        Map<UUID, LedgerAccount> accountsById = new HashMap<>();
        if (command.getEntries() == null) {
            return accountsById;
        }
        for (PostingEntryCommand entryCommand : command.getEntries()) {
            UUID ledgerAccountId = entryCommand.getLedgerAccountId();
            if (accountsById.containsKey(ledgerAccountId)) {
                continue;
            }
            LedgerAccount ledgerAccount = ledgerAccountRepository.findById(command.getTenantId(), ledgerAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("account not found: " + ledgerAccountId));
            accountsById.put(ledgerAccountId, ledgerAccount);
        }
        return accountsById;
    }
}
