package com.sistema.wallet.api.dto;

import java.util.List;

public class WalletStatementResponse {
    private String accountId;
    private List<WalletStatementItemResponse> items;
    private int page;
    private int size;
    private long total;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<WalletStatementItemResponse> getItems() {
        return items;
    }

    public void setItems(List<WalletStatementItemResponse> items) {
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
