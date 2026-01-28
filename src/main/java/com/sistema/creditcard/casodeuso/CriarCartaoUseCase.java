package com.sistema.creditcard.casodeuso;

import com.sistema.creditcard.adaptadores.dto.CreditCardDTO;
import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import com.sistema.creditcard.dominio.servico.CartaoDeCreditoService;
import com.sistema.creditcard.infraestrutura.mapper.CartaoDeCreditoMapper;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class CriarCartaoUseCase {
    private static final UUID DEFAULT_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Inject
    CartaoDeCreditoService cartaoDeCreditoService;

    @Inject
    CartaoRepository cartaoDeCreditoRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CartaoDeCreditoMapper cartaoDeCreditoMapper;

    @Transactional
    public CreditCard executar(CreditCardDTO cartaoDTO) {
        Customer customer = customerRepository.findById(DEFAULT_TENANT_ID, UUID.fromString(cartaoDTO.getClienteId()))
                .orElseThrow(() -> new IllegalArgumentException("Cliente não Encontrado:" + cartaoDTO.getClienteId()));

        CreditCard cartaoCriado = cartaoDeCreditoService.criarCartao(
                cartaoDTO.getBandeira(),
                cartaoDTO.getNomeTitular(),
                LocalDate.now().plusYears(5),
                cartaoDTO.getCvv(),
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                customer
        );

        return cartaoDeCreditoRepository.save(cartaoCriado);
    }
}

