package com.sistema.casodeuso;

import com.sistema.adaptadores.dto.CreditCardDTO;
import com.sistema.dominio.entidade.CreditCard;
import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.servico.CartaoDeCreditoService;
import com.sistema.infraestrutura.mapper.CartaoDeCreditoMapper;
import com.sistema.infraestrutura.mapper.CustomerMapper;
import com.sistema.dominio.repository.CartaoRepository;

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
    CartaoRepository cartaoDeCreditoRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CustomerMapper customerMapper;

    @Inject
    CartaoDeCreditoMapper cartaoDeCreditoMapper;

    @Transactional
    public CreditCard executar(CreditCardDTO cartaoDTO) {

        System.out.println("Recebido DTO bandeira: " + cartaoDTO.getBandeira());

        Customer customer = customerRepository.findById((UUID.fromString(cartaoDTO.getClienteId())));

        if (customer == null) {
            throw new IllegalArgumentException("Cliente não Encontrado:" + cartaoDTO.getClienteId());
        }

            CreditCard cartaoCriado = cartaoDeCreditoService.criarCartao(
                cartaoDTO.getBandeira(),
                cartaoDTO.getNomeTitular(),
                LocalDate.now().plusYears(5),
                cartaoDTO.getCvv(),
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                    customer
        );

        System.out.println("Recebido cartaoCriado bandeira: " + cartaoCriado.getBandeira());

        return cartaoDeCreditoRepository.save(cartaoCriado);
    }
}
