package com.sistema.customer.api.dto;

import com.sistema.customer.domain.model.Customer;

import java.time.Instant;
import java.util.UUID;

public class CustomerResponse {
    private UUID id;
    private String type;
    private String name;
    private String documentType;
    private String documentNumber;
    private String status;
    private Instant createdAt;

    public static CustomerResponse from(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setType(customer.getType().name());
        response.setName(customer.getName());
        response.setDocumentType(customer.getDocumentType().name());
        response.setDocumentNumber(customer.getDocumentNumber());
        response.setStatus(customer.getStatus().name());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

