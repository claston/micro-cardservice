package com.sistema.creditcard.casodeuso;

import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.dominio.entidade.Transacao;
import com.sistema.creditcard.dominio.repository.FaturaRepository;
import com.sistema.creditcard.dominio.servico.FaturaService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class RegistarTransacaoNaFaturaUseCase {

    @Inject
    FaturaRepository faturaRepository;

    FaturaService faturaService = new FaturaService();

    public Fatura registarTransacaoNaFatura(Transacao transacao){

        LocalDate periodoTransacao = transacao.getDataHora().toLocalDate().withDayOfMonth(1);

        //Procura pela fatura do mês. Se não encontrar cria uma nova fatura.
        Fatura fatura = faturaRepository.findByMesAno(periodoTransacao)
                .orElse(new Fatura(periodoTransacao));

        faturaService.adicionarTransacao(fatura, transacao);

        Fatura faturaSalva = faturaRepository.save(fatura);

        return faturaSalva;
    }
}


