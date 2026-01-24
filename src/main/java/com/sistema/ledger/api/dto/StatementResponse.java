package com.sistema.ledger.api.dto;

import java.util.List;
import java.util.UUID;

public class StatementResponse {
    private UUID accountId;
    private List<StatementItemResponse> items;
    private int page;
    private int size;
    private long total;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public List<StatementItemResponse> getItems() {
        return items;
    }

    public void setItems(List<StatementItemResponse> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
