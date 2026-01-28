package com.sistema.creditcard.util;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.servico.CartaoDeCreditoService;
import com.sistema.creditcard.dominio.servico.GeradorNumeroCartao;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.repository.CustomerRepository;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Dependent
public class CartaoDeCreditoBuilder {

    private Customer customer;
    private boolean persistirCliente = false;
    private UUID cartaoId;
    private GeradorNumeroCartao geradorNumeroCartao;

    @Inject
    private CustomerRepository customerRepository;

    public CartaoDeCreditoBuilder comID(UUID cartaoId) {
        this.cartaoId = cartaoId;
        return this;
    }

    public CartaoDeCreditoBuilder comCliente(Customer customer) {
        this.customer = customer;
        return this;
    }

    public CartaoDeCreditoBuilder comGeradorNumeroCartao(GeradorNumeroCartao geradorNumeroCartao) {
        this.geradorNumeroCartao = geradorNumeroCartao;
        return this;
    }

    public CartaoDeCreditoBuilder persistindoCliente() {
        this.persistirCliente = true;
        return this;
    }

    @Transactional
    public CreditCard build() {
        if (persistirCliente && customer != null) {
            this.customer = customerRepository.save(customer);
        }

        GeradorNumeroCartao gerador = this.geradorNumeroCartao != null
                ? this.geradorNumeroCartao
                : new GeradorNumeroCartao();
        CartaoDeCreditoService cartaoDeCreditoService = new CartaoDeCreditoService(gerador);

        CreditCard cartao = cartaoDeCreditoService.criarCartao(
                "Mastercard",
                "Joao da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                customer);

        if (this.cartaoId != null) {
            cartao.setId(this.cartaoId);
        }

        return cartao;
    }
}

