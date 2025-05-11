package com.sistema.infraestrutura.entidade;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="fatura")
public class FaturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private BigDecimal total;
    private BigDecimal pagamentoMinimo;
    private BigDecimal valorEmAberto;
    private LocalDate mesAno;
    private boolean paga;

    @OneToMany(mappedBy = "fatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransacaoEntity> transacoes = new ArrayList<>();

    public FaturaEntity(){}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPagamentoMinimo() {
        return pagamentoMinimo;
    }

    public void setPagamentoMinimo(BigDecimal pagamentoMinimo) {
        this.pagamentoMinimo = pagamentoMinimo;
    }

    public LocalDate getMesAno() {
        return mesAno;
    }

    public void setMesAno(LocalDate mesAno) {
        this.mesAno = mesAno;
    }

    public boolean isPaga() {
        return paga;
    }

    public void setPaga(boolean paga) {
        this.paga = paga;
    }

    public List<TransacaoEntity> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoEntity> transacoes) {
        this.transacoes = transacoes;
    }

    public void addTransacao(TransacaoEntity transacao){
        this.transacoes.add(transacao);
        transacao.setFatura(this);
    }

    public void removeTransacao(TransacaoEntity transacao){
        this.transacoes.remove(transacao);
        transacao.setFatura(null);
    }

    public BigDecimal getValorEmAberto() {
        return valorEmAberto;
    }

    public void setValorEmAberto(BigDecimal valorEmAberto) {
        this.valorEmAberto = valorEmAberto;
    }
}
