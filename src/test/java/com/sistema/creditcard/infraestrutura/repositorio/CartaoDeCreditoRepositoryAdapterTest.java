package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import com.sistema.creditcard.dominio.servico.GeradorNumeroCartao;
import com.sistema.creditcard.util.CartaoDeCreditoBuilder;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerStatus;
import com.sistema.customer.domain.model.CustomerType;
import com.sistema.customer.domain.repository.CustomerRepository;
import com.sistema.infraestrutura.repositorio.DbCleanIT;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class CartaoDeCreditoRepositoryAdapterTest extends DbCleanIT {

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CartaoRepository cartaoRepository;

    @Test
    @Transactional
    @Tag("integration-test")
    public void shouldCreateValidCreditCardWhenClientExistsInDatabase() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        customer.setType(CustomerType.INDIVIDUAL);
        customer.setName("Joao da Silva");
        customer.setDocumentType(CustomerDocumentType.CPF);
        customer.setDocumentNumber("12345678901");
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        Customer customerSaved = customerRepository.save(customer);

        GeradorNumeroCartao geradorNumeroCartao = mock(GeradorNumeroCartao.class);
        when(geradorNumeroCartao.gerarNumero()).thenReturn("1111222233334444");

        CreditCard card = new CartaoDeCreditoBuilder()
                .comCliente(customerSaved)
                .comGeradorNumeroCartao(geradorNumeroCartao)
                .build();

        CreditCard savedCard = cartaoRepository.save(card);

        assertNotNull(savedCard);
        assertNotNull(savedCard.getCliente().getId());
        assertEquals("Mastercard", savedCard.getBandeira());
        assertEquals("Joao da Silva", savedCard.getNomeTitular());
        assertEquals("1111222233334444", savedCard.getNumero());
        assertEquals(new BigDecimal("1000.00"), savedCard.getLimiteTotal());
        assertEquals(new BigDecimal("1000.00"), savedCard.getLimiteDisponivel());
    }
}
