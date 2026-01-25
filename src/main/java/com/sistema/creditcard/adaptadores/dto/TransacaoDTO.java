package com.sistema.creditcard.adaptadores.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransacaoDTO {
    private String descricao;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private UUID cartaoId;

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

    public UUID getCartaoId() {
        return cartaoId;
    }

    public void setCartaoId(UUID cartaoId) {
        this.cartaoId = cartaoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}

