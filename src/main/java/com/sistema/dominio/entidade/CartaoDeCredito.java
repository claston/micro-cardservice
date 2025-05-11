package com.sistema.dominio.entidade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CartaoDeCredito {

    private UUID id;
    private String numero;
    private String bandeira;

    private String nomeTitular;
    private LocalDate dataValidade;
    private String cvv;

    private BigDecimal limiteTotal = BigDecimal.ZERO;
    private BigDecimal limiteDisponivel = BigDecimal.ZERO;
    private BigDecimal saldoDevedor = BigDecimal.ZERO;

    private boolean ativo;
    private boolean bloqueadoPorPerdaOuRoubo;

    private Cliente cliente;

    public CartaoDeCredito() {}

    public CartaoDeCredito(String numero, String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel) {

        if (nomeTitular == null || nomeTitular.isEmpty()) {
           throw new IllegalArgumentException("Nome do titular do cartão não pode ser nulo ou vazio");
        }
        if (numero == null || numero.length() != 16) {
            throw new IllegalArgumentException("Número do cartão inválido");
        }

        if (limiteDisponivel.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O limite deve ser maior que zero.");
        }

        if (limiteTotal.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O limite deve ser maior que zero.");
        }

        this.numero = numero;
        this.bandeira = bandeira;
        this.nomeTitular = nomeTitular;
        this.dataValidade = dataValidade;
        this.cvv = cvv;
        this.limiteTotal = limiteTotal;
        this.limiteDisponivel = limiteDisponivel;
        this.ativo = true;
        this.bloqueadoPorPerdaOuRoubo = false;
    }

    public CartaoDeCredito(String numero, String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel, Cliente cliente) {

        if (nomeTitular == null || nomeTitular.isEmpty()) {
            throw new IllegalArgumentException("Nome do titular do cartão não pode ser nulo ou vazio");
        }
        if (numero == null || numero.length() != 16) {
            throw new IllegalArgumentException("Número do cartão inválido");
        }

        if (limiteDisponivel.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O limite deve ser maior que zero.");
        }

        if (limiteTotal.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O limite deve ser maior que zero.");
        }

        this.numero = numero;
        this.bandeira = bandeira;
        this.nomeTitular = nomeTitular;
        this.dataValidade = dataValidade;
        this.cvv = cvv;
        this.limiteTotal = limiteTotal;
        this.limiteDisponivel = limiteDisponivel;
        this.ativo = true;
        this.bloqueadoPorPerdaOuRoubo = false;
        this.cliente = cliente;
    }

    // Métodos para transações
    public boolean realizarCompra(BigDecimal valor){

        if(!ativo || bloqueadoPorPerdaOuRoubo
                || valor.compareTo(limiteDisponivel) > 0
                || valor.compareTo(BigDecimal.ZERO) == 0) {
            return false; // Transação Negada
        }

        limiteDisponivel = limiteDisponivel.subtract(valor);
        saldoDevedor = saldoDevedor.add(valor);
        return true; // transação aprovada
    }

    public void realizarPagamento(BigDecimal valor){
        saldoDevedor = saldoDevedor.subtract(valor) ;

        if (saldoDevedor.compareTo(BigDecimal.ZERO) < 0) {
            saldoDevedor = BigDecimal.ZERO;
        }

        calcularLimiteDisponivel();
    }

    public void calcularLimiteDisponivel() {
        this.limiteDisponivel = limiteTotal.subtract(saldoDevedor);
    }

    public void bloquearCartao(){
        this.ativo = false;
    }

    public void desbloquearCartao(){
        this.ativo = true;
    }

    public void exibirInformacoes() {
        System.out.println("Cartão: **** **** **** " + numero.substring(numero.length() - 4));
        System.out.println("Titular: " + nomeTitular);
        System.out.println("Bandeira: " + bandeira);
        System.out.println("Limite Disponível: R$ " + limiteDisponivel);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBandeira() {
        return bandeira;
    }

    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public BigDecimal getLimiteTotal() {
        return limiteTotal;
    }

    public void setLimiteTotal(BigDecimal limiteTotal) {
        this.limiteTotal = limiteTotal;
    }

    public BigDecimal getLimiteDisponivel() {
        return limiteDisponivel;
    }

    public void setLimiteDisponivel(BigDecimal limiteDisponivel) {
        this.limiteDisponivel = limiteDisponivel;
    }

    public BigDecimal getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(BigDecimal saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isBloqueadoPorPerdaOuRoubo() {
        return bloqueadoPorPerdaOuRoubo;
    }

    public void setBloqueadoPorPerdaOuRoubo(boolean bloqueadoPorPerdaOuRoubo) {
        this.bloqueadoPorPerdaOuRoubo = bloqueadoPorPerdaOuRoubo;
    }


    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
