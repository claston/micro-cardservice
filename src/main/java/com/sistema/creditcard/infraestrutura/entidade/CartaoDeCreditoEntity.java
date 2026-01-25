package com.sistema.creditcard.infraestrutura.entidade;

import com.sistema.customer.infraestrutura.entidade.CustomerEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class CartaoDeCreditoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String numero;
    private String bandeira;

    private String nomeTitular;
    private LocalDate dataValidade;
    private String cvv;

    private BigDecimal limiteTotal;
    private BigDecimal limiteDisponivel;
    private BigDecimal saldoDevedor;

    private boolean ativo;
    private boolean bloqueadoPorPerdaOuRoubo;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "cliente_id", nullable = false)
    private CustomerEntity cliente;

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

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
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

    public CustomerEntity getCliente() {
        return cliente;
    }

    public void setCliente(CustomerEntity cliente) {
        this.cliente = cliente;
    }
}

