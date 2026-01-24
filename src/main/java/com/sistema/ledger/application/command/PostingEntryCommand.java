package com.sistema.ledger.application.command;

import com.sistema.ledger.domain.model.EntryDirection;

import java.util.UUID;

public class PostingEntryCommand {
    private final UUID accountId;
    private final EntryDirection direction;
    private final long amountMinor;
    private final String currency;

    public PostingEntryCommand(UUID accountId, EntryDirection direction, long amountMinor, String currency) {
        this.accountId = accountId;
        this.direction = direction;
        this.amountMinor = amountMinor;
        this.currency = currency;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public EntryDirection getDirection() {
        return direction;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public String getCurrency() {
        return currency;
    }
}
