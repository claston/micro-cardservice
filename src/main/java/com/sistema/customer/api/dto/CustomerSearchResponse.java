package com.sistema.customer.api.dto;

import java.util.List;

public class CustomerSearchResponse {
    private List<CustomerResponse> items;
    private int page;
    private int size;
    private long total;

    public List<CustomerResponse> getItems() {
        return items;
    }

    public void setItems(List<CustomerResponse> items) {
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

