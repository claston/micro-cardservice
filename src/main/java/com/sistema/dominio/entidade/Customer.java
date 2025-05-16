package com.sistema.dominio.entidade;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Customer {

    private UUID id;
    private String cpf;
    private String cnpj;

    private String name;
    private LocalDate dataNascimento;

    private String email;
    private String foneNumber;

    private boolean ativo;
    private LocalDate dataCadastro;

   //private List<CartaoDeCredito> cartoes = new ArrayList<>();

    public Customer() {
        this.ativo = true;
        this.dataCadastro = LocalDate.now();
    }

    public Customer(String cpf, String name, String email ) {
        this.cpf = cpf;
        this.name = name;
        this.email = email;
        this.ativo = true;
        this.dataCadastro = LocalDate.now();
    }

    public Customer(String name, String cpf) {
        this.cpf = cpf;
        this.name = name;
        this.ativo = true;
        this.dataCadastro = LocalDate.now();
    }

    // Static factory method with validation
    public static Customer createValidCustomer(String name, String email, LocalDate registrationDate) {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(registrationDate, "Registration Date cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        return new Customer(
                "12345678890",
                name.trim(),
                email.toLowerCase().trim()
        );
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoneNumber() {
        return foneNumber;
    }

    public void setFoneNumber(String foneNumber) {
        this.foneNumber = foneNumber;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

//    public List<CartaoDeCredito> getCartoes() {
//        return cartoes;
//    }
//
//    public void setCartoes(List<CartaoDeCredito> cartoes) {
//        this.cartoes = cartoes;
//    }
//
//    public void adicionarCartao(CartaoDeCredito cartao) {
//        this.cartoes.add(cartao);
//    }
}

