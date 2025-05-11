package com.sistema.dominio.entidade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Fatura {

    private UUID id;
    private BigDecimal total;
    private BigDecimal pagamentoMinimo;
    private LocalDate mesAno;
    private boolean paga;
    private BigDecimal valorEmAberto = BigDecimal.ZERO;
    
    private List<Transacao> transacoes = new ArrayList<>();

    public Fatura() {

    }

    public Fatura(LocalDate mesAno) {
        this.mesAno = mesAno;
    }

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

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    /**
     * Adiciona um transação à fatura
     * Essa operação não recalcula os totais automáticamente
    */
    public void addTransacao(Transacao transacao) {
        if (!this.transacoes.contains(transacao)){
            this.transacoes.add(transacao);
            // Só faça a associação caso não esteja associada a essa fatura
//            if(transacao.getFatura() != this){
//                transacao.setFatura(this);
//            }
        }
    }

    /**
     * Calcula o total da fatura e o pagmento mínimo
     */
    public void calculaTotais(){
        this.total = BigDecimal.ZERO;
        for (Transacao t : transacoes) {
            this.total = this.total.add(t.getValor());
            if (!this.isPaga()){
                this.valorEmAberto = this.valorEmAberto.add(t.getValor());
            }
        }

        this.pagamentoMinimo = this.total
                .multiply(new BigDecimal("0.15"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void pagar(BigDecimal bigDecimal) {
        this.paga = true;
        this.valorEmAberto = BigDecimal.ZERO;
    }

    public BigDecimal getValorEmAberto() {
        return valorEmAberto;
    }

    public void setValorEmAberto(BigDecimal valorEmAberto) {
        this.valorEmAberto = valorEmAberto;
    }
}
