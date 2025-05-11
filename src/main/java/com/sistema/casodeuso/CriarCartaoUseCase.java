package com.sistema.casodeuso;

import com.sistema.adaptadores.dto.CartaoDeCreditoDTO;
import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.dominio.servico.CartaoDeCreditoService;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.mapper.CartaoDeCreditoMapper;
import com.sistema.infraestrutura.mapper.ClienteMapper;
import com.sistema.infraestrutura.repositorio.CartaoDeCreditoRepository;

import com.sistema.dominio.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class CriarCartaoUseCase {

    @Inject
    CartaoDeCreditoService cartaoDeCreditoService;

    @Inject
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    ClienteMapper clienteMapper;

    @Inject
    CartaoDeCreditoMapper cartaoDeCreditoMapper;

    @Transactional
    public CartaoDeCredito executar(CartaoDeCreditoDTO cartaoDTO) {

        System.out.println("Recebido DTO bandeira: " + cartaoDTO.getBandeira());

        Cliente cliente = customerRepository.findById((UUID.fromString(cartaoDTO.getClienteId())));

        if (cliente == null) {

            throw new IllegalArgumentException("Cliente não Encontrado:" + cartaoDTO.getClienteId());

        }

        System.out.println("Recebido clienteMapper bandeira: " + cliente.getNome());

        CartaoDeCredito cartaoCriado = cartaoDeCreditoService.criarCartao(
                cartaoDTO.getBandeira(),
                cartaoDTO.getNomeTitular(),
                LocalDate.now().plusYears(5),
                cartaoDTO.getCvv(),
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                cliente
        );

        System.out.println("Recebido cartaoCriado bandeira: " + cartaoCriado.getBandeira());

        var entity = cartaoDeCreditoMapper.toEntity(cartaoCriado);

       System.out.println("Recebido bandeira Entity: " + entity.getBandeira());

        cartaoDeCreditoRepository.persist(entity);

        //System.out.println("ID Cartão Gerado: " + entity.getId()); // Verifica se o ID foi gerado
       // System.out.println("ID Cliente: " + entity.getCliente().getId());
        return cartaoDeCreditoMapper.toDomain(entity);

    }
}
