package com.sistema.wallet.application.model;

import java.util.List;
import java.util.UUID;

public class WalletStatementPage {
    private final UUID accountId;
    private final List<WalletStatementItem> items;
    private final int page;
    private final int size;
    private final long total;

    public WalletStatementPage(UUID accountId, List<WalletStatementItem> items, int page, int size, long total) {
        this.accountId = accountId;
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public List<WalletStatementItem> getItems() {
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
