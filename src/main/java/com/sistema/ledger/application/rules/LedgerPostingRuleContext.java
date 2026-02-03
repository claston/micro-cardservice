package com.sistema.ledger.application.rules;

import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.LedgerAccount;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LedgerPostingRuleContext {
    private final PostLedgerTransactionCommand command;
    private final Map<UUID, LedgerAccount> accountsById;
    private final List<Entry> entries;
    private final Instant occurredAt;
    private final Instant createdAt;

    public LedgerPostingRuleContext(PostLedgerTransactionCommand command,
                                    Map<UUID, LedgerAccount> accountsById,
                                    List<Entry> entries,
                                    Instant occurredAt,
                                    Instant createdAt) {
        this.command = command;
        this.accountsById = accountsById;
        this.entries = entries;
        this.occurredAt = occurredAt;
        this.createdAt = createdAt;
    }

    public PostLedgerTransactionCommand getCommand() {
        return command;
    }

    public Map<UUID, LedgerAccount> getAccountsById() {
        return accountsById;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
