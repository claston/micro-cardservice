package com.sistema.ledger.application.model;

import java.util.List;
import java.util.UUID;

public class StatementPage {
    private final UUID accountId;
    private final List<StatementItem> items;
    private final int page;
    private final int size;
    private final long total;

    public StatementPage(UUID accountId, List<StatementItem> items, int page, int size, long total) {
        this.accountId = accountId;
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public List<StatementItem> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }
}
