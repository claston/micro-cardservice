package com.sistema.dominio.entidade;

import com.sistema.dominio.servico.CartaoDeCreditoService;
import com.sistema.dominio.servico.GeradorNumeroCartao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CartaoDeCreditoTestFactory {

        public static CreditCard criaCartaoValido(){

            Customer customer = new Customer();
            customer.setName("João Silva");

            CartaoDeCreditoService cartaoDeCreditoService = new CartaoDeCreditoService(new GeradorNumeroCartao());

            return cartaoDeCreditoService.criarCartao(
                    "Mastercard",
                    "João da Silva",
                    LocalDate.now().plusYears(5),
                    "123",
                    new BigDecimal("1000.00"),
                    new BigDecimal("1000.00"),
                    customer);
        }

    public static CreditCard criaCartaoValido(UUID cartaoId){

        Customer customer = new Customer();
        customer.setName("João Silva");

        CartaoDeCreditoService cartaoDeCreditoService = new CartaoDeCreditoService(new GeradorNumeroCartao());

        return cartaoDeCreditoService.criarCartao(
                cartaoId,
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00")
        );
    }
}
