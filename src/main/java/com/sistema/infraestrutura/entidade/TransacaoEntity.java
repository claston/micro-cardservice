package com.sistema.infraestrutura.entidade;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="transacoes")
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String descricao;
    private BigDecimal valor;
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "cartao_id", nullable = false)
    private CartaoDeCreditoEntity cartao;

    @ManyToOne
    @JoinColumn(name = "fatura_id")
    private FaturaEntity fatura;

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

    public CartaoDeCreditoEntity getCartao() {
        return cartao;
    }

    public void setCartao(CartaoDeCreditoEntity cartao) {
        this.cartao = cartao;
    }

    public FaturaEntity getFatura() {
        return fatura;
    }

    /*
     * Associa ou desassocia uma transação de uma fatura.
     *
     * @param fatura a Fatura Entity a qual pertence a transação ou null para desassociar.
     */
    public void setFatura(FaturaEntity fatura) {
        this.fatura = fatura;
    }
}
