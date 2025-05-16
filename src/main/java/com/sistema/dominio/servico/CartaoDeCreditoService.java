package com.sistema.dominio.servico;

import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class CartaoDeCreditoService {

    private final GeradorNumeroCartao geradorNumeroCartao;

    @Inject
    public CartaoDeCreditoService(GeradorNumeroCartao geradorNumeroCartao){
        this.geradorNumeroCartao = geradorNumeroCartao;
    }

    public CartaoDeCredito criarCartao(String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel) {
        String numero = geradorNumeroCartao.gerarNumero();
        UUID cartaoID = UUID.randomUUID();
        return new CartaoDeCredito(numero, bandeira, nomeTitular, dataValidade, cvv, limiteTotal, limiteDisponivel);
    }

    public CartaoDeCredito criarCartao(UUID cartaoID, String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel) {
        String numero = geradorNumeroCartao.gerarNumero();
        return new CartaoDeCredito(numero, bandeira, nomeTitular, dataValidade, cvv, limiteTotal, limiteDisponivel);
    }

    public CartaoDeCredito criarCartao(String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel, Customer customer) {
        String numero = geradorNumeroCartao.gerarNumero();
        return new CartaoDeCredito(numero, bandeira, nomeTitular, dataValidade, cvv, limiteTotal, limiteDisponivel, customer);
    }
}



