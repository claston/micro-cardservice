package com.sistema.dominio.entidade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transacao {
    private UUID id;
    private String descricao;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private CartaoDeCredito cartao;
    private Fatura fatura;

    public Transacao(String descricao, BigDecimal valor, CartaoDeCredito cartao, LocalDateTime dataHora){

        if(descricao == null || descricao.isEmpty()){
            throw new IllegalArgumentException(("Descrição da transação não pode ser nula ou vazia"));
        }

        if(valor == null || valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }

        if(cartao == null){
            throw new IllegalArgumentException("Cartão não pode ser nulo.");
        }

        this.descricao = descricao;
        this.valor = valor;
        this.dataHora = dataHora;
        this.cartao = cartao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public CartaoDeCredito getCartao() {
        return cartao;
    }

    public void setCartao(CartaoDeCredito cartao) {
        this.cartao = cartao;
    }

    public Fatura getFatura() {
        return fatura;
    }

    public void setFatura(Fatura fatura) {
        if (this.fatura == fatura){
            return;
        }
        this.fatura = fatura;
        // Se a fatura não estiver nula e não contiver essa transação, adicione-a
        if(fatura != null && !fatura.getTransacoes().contains(this)){
            fatura.addTransacao(this);
        }
    }
}
