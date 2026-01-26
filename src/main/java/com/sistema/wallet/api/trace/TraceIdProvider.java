package com.sistema.wallet.api.trace;

import jakarta.enterprise.context.RequestScoped;

import java.util.UUID;

@RequestScoped
public class TraceIdProvider {
    private String traceId;

    public String getTraceId() {
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
