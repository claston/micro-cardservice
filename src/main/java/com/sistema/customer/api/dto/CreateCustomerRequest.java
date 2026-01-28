package com.sistema.customer.api.dto;

import com.sistema.common.api.validation.ValidEnum;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerType;
import jakarta.validation.constraints.NotBlank;

public class CreateCustomerRequest {
    @NotBlank(message = "type is required")
    @ValidEnum(enumClass = CustomerType.class, message = "type must be one of: INDIVIDUAL, BUSINESS")
    private String type;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "documentType is required")
    @ValidEnum(enumClass = CustomerDocumentType.class, message = "documentType must be one of: CPF, CNPJ")
    private String documentType;

    @NotBlank(message = "documentNumber is required")
    private String documentNumber;

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
}

