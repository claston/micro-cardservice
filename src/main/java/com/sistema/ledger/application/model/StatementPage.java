package com.sistema.ledger.application.model;

import java.util.List;
import java.util.UUID;

public class StatementPage {
    private final UUID ledgerAccountId;
    private final List<StatementItem> items;
    private final int page;
    private final int size;
    private final long total;

    public StatementPage(UUID ledgerAccountId, List<StatementItem> items, int page, int size, long total) {
        this.ledgerAccountId = ledgerAccountId;
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public UUID getLedgerAccountId() {
        return ledgerAccountId;
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
