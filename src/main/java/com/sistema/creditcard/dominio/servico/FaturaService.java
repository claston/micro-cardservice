package com.sistema.creditcard.dominio.servico;

import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.dominio.entidade.Transacao;
import java.time.LocalDate;

public class FaturaService {

    /**
     * Adiciona uma transação à fatura.
     * Se a transação não pertencer ao mesmo mês da fatura, pode-se lançar  uma execção ou tratá-la conforme a regra de negócio.
     */
    public void adicionarTransacao(Fatura fatura, Transacao transacao){

        LocalDate periodoTransacao = transacao.getDataHora().toLocalDate().withDayOfMonth(1);

        if (!fatura.getMesAno().equals(periodoTransacao)){
            throw new IllegalArgumentException("A transação não pertence ao período da  ffffatura");
        }

        fatura.addTransacao(transacao);
        fatura.calculaTotais();
    }
}


