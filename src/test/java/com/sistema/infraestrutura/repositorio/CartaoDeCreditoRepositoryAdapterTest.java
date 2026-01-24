package com.sistema.infraestrutura.repositorio;

import com.sistema.dominio.entidade.CreditCard;
import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.repository.CartaoRepository;
import com.sistema.dominio.repository.CustomerRepository;
import com.sistema.dominio.servico.GeradorNumeroCartao;
import com.sistema.util.CartaoDeCreditoBuilder;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
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
        //Arrange
        LocalDate registrationDate = LocalDate.now();
        Customer customer = Customer.createValidCustomer("João da Silva", "joao@teste.com.br", registrationDate);
        Customer customerSaved = customerRepository.save(customer);

        GeradorNumeroCartao geradorNumeroCartao = mock(GeradorNumeroCartao.class);
        when(geradorNumeroCartao.gerarNumero()).thenReturn("1111222233334444");

        CreditCard card = new CartaoDeCreditoBuilder()
                .comCliente(customerSaved)
                .comGeradorNumeroCartao(geradorNumeroCartao)
                .build();
        //Act
        CreditCard savedCard = cartaoRepository.save(card);

        //Assert
        assertNotNull(savedCard);
        assertNotNull(savedCard.getCliente().getDataCadastro());
        assertTrue((savedCard.getCliente().isAtivo()));
        assertEquals("Mastercard", savedCard.getBandeira());
        assertEquals("João da Silva", savedCard.getNomeTitular());
        assertEquals("1111222233334444", savedCard.getNumero());
        assertEquals(new BigDecimal("1000.00"), savedCard.getLimiteTotal());
        assertEquals(new BigDecimal("1000.00"), savedCard.getLimiteDisponivel());
    }
}
