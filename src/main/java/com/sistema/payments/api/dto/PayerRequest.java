package com.sistema.payments.api.dto;

import jakarta.validation.constraints.NotBlank;

public class PayerRequest {
    @NotBlank(message = "payer.name is required")
    private String name;

    @NotBlank(message = "payer.document is required")
    private String document;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
