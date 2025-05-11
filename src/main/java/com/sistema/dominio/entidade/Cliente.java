package com.sistema.dominio.entidade;

import java.time.LocalDate;
import java.util.UUID;

public class Cliente {

    private UUID id;
    private String cpf;
    private String cnpj;

    private String nome;
    private LocalDate dataNascimento;

    private String email;
    private String telefone;

    private boolean ativo;
    private LocalDate dataCadastro;

   //private List<CartaoDeCredito> cartoes = new ArrayList<>();

    public Cliente() {
        this.ativo = true;
        this.dataCadastro = LocalDate.now();
    }

    public Cliente (String cpf, String nome, String email ) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.ativo = true;
        this.dataCadastro = LocalDate.now();
    }

    public Cliente(String nome, String cpf) {
        this.cpf = cpf;
        this.nome = nome;
        this.ativo = true;
        this.dataCadastro = LocalDate.now();
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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

