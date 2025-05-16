package com.sistema.util;

import com.sistema.dominio.entidade.CreditCard;
import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.servico.CartaoDeCreditoService;
import com.sistema.dominio.servico.GeradorNumeroCartao;
import com.sistema.dominio.repository.CustomerRepository;
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

    @Inject
    private CustomerRepository customerRepository;

    public CartaoDeCreditoBuilder comID(UUID cartaoId){
        this.cartaoId = cartaoId;
        return this;
    }

    public CartaoDeCreditoBuilder comCliente(Customer customer){
        this.customer = customer;
        return this;
    }

    public CartaoDeCreditoBuilder persistindoCliente(){
        this.persistirCliente = true;
        return this;
    }

    @Transactional
    public CreditCard build() {

        if (persistirCliente && customer != null) {
            this.customer = customerRepository.save(customer);
        }

        CartaoDeCreditoService cartaoDeCreditoService = new CartaoDeCreditoService(new GeradorNumeroCartao());

        CreditCard cartao = cartaoDeCreditoService.criarCartao(
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                customer);

        System.out.println("CARTAO_ID:" + cartao.getId());

        if (this.cartaoId != null) {
            cartao.setId(this.cartaoId);
        }

        System.out.println("CARTAO_ID_SET_ID:" + cartao.getId());

        return cartao;
    }
}
