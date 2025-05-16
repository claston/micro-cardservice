package com.sistema.infraestrutura.repositorio;

import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.repository.CartaoRepository;
import com.sistema.dominio.repository.CustomerRepository;
import com.sistema.util.CartaoDeCreditoBuilder;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CartaoDeCreditoRepositoryAdapterTest {

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CartaoRepository cartaoRepository;

    public void shouldCreateValidCreditCardWhenClientExistsInDatabase() {
        //Arrange
        LocalDate registrationDate = LocalDate.now();
        Customer customer = Customer.createValidCustomer("João da Silva", "joao@teste.com.br", registrationDate);
        customerRepository.save(customer);

        CartaoDeCredito card = new CartaoDeCreditoBuilder().comCliente(customer).build();
        //Act
        CartaoDeCredito savedCard = cartaoRepository.save(card);

        //Assert
        assertNotNull(savedCard);
        assertNotNull(savedCard.getCliente().getDataCadastro());
        assertTrue((savedCard.getCliente().isAtivo()));
        assertEquals("Mastercard", savedCard.getBandeira());
        assertEquals("João Silva", savedCard.getNomeTitular());
        assertEquals("1111222233334444", savedCard.getNumero());
        assertEquals(new BigDecimal("1000.00"), savedCard.getLimiteTotal());
        assertEquals(new BigDecimal("1000.00"), savedCard.getLimiteDisponivel());
    }
}