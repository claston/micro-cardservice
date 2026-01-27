package com.sistema.customer.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CustomerDTO {
    @JsonAlias("nome")
    private String name;
    private String cpf;
    private String email;
    @JsonAlias({"telefone", "foneNumber"})
    private String phoneNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

