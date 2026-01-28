package com.sistema.customer.application.command;

public class CreateCustomerCommand {
    private String type;
    private String name;
    private String documentType;
    private String documentNumber;

    public CreateCustomerCommand() {
    }

    public CreateCustomerCommand(String type, String name, String documentType, String documentNumber) {
        this.type = type;
        this.name = name;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }
}

