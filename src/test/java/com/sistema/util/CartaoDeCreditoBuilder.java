package com.sistema.util;

import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.dominio.servico.CartaoDeCreditoService;
import com.sistema.dominio.servico.GeradorNumeroCartao;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.mapper.ClienteMapper;
import com.sistema.infraestrutura.repositorio.ClienteRepository;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Dependent
public class CartaoDeCreditoBuilder {

    private Cliente cliente;
    private boolean persistirCliente = false;
    private UUID cartaoId;

    @Inject
    private ClienteMapper clienteMapper;

    @Inject
    private ClienteRepository clienteRepository;

    public CartaoDeCreditoBuilder comID(UUID cartaoId){
        this.cartaoId = cartaoId;
        return this;
    }

    public CartaoDeCreditoBuilder comCliente(Cliente cliente){
        this.cliente = cliente;
        return this;
    }

    public CartaoDeCreditoBuilder persistindoCliente(){
        this.persistirCliente = true;
        return this;
    }

    @Transactional
    public CartaoDeCredito build() {

        if (persistirCliente && cliente != null) {
            ClienteEntity entity = clienteMapper.toEntity(cliente);
            clienteRepository.persist(entity);
          //  clienteRepository.getEntityManager().flush();
            this.cliente = clienteMapper.toDomain(entity);
        }

        CartaoDeCreditoService cartaoDeCreditoService = new CartaoDeCreditoService(new GeradorNumeroCartao());

        CartaoDeCredito cartao = cartaoDeCreditoService.criarCartao(
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                cliente);

        System.out.println("CARTAO_ID:" + cartao.getId());

        if (this.cartaoId != null) {
            cartao.setId(this.cartaoId);
        }

        System.out.println("CARTAO_ID_SET_ID:" + cartao.getId());

        return cartao;
    }
}
