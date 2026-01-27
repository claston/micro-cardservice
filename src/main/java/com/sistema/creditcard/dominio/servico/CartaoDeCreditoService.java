package com.sistema.creditcard.dominio.servico;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.customer.domain.model.Customer;
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

    public CreditCard criarCartao(String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel) {
        String numero = geradorNumeroCartao.gerarNumero();
        UUID cartaoID = UUID.randomUUID();
        return new CreditCard(numero, bandeira, nomeTitular, dataValidade, cvv, limiteTotal, limiteDisponivel);
    }

    public CreditCard criarCartao(UUID cartaoID, String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel) {
        String numero = geradorNumeroCartao.gerarNumero();
        return new CreditCard(numero, bandeira, nomeTitular, dataValidade, cvv, limiteTotal, limiteDisponivel);
    }

    public CreditCard criarCartao(String bandeira, String nomeTitular, LocalDate dataValidade, String cvv, BigDecimal limiteTotal, BigDecimal limiteDisponivel, Customer customer) {
        String numero = geradorNumeroCartao.gerarNumero();
        return new CreditCard(numero, bandeira, nomeTitular, dataValidade, cvv, limiteTotal, limiteDisponivel, customer);
    }
}






