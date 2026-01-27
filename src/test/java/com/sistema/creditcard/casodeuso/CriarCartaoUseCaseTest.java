package com.sistema.creditcard.casodeuso;

import com.sistema.creditcard.adaptadores.dto.CreditCardDTO;
import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.customer.domain.model.Customer;
import com.sistema.creditcard.dominio.servico.CartaoDeCreditoService;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import com.sistema.customer.domain.repository.CustomerRepository;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
public class CriarCartaoUseCaseTest {

    @InjectMock
    CustomerRepository customerRepository;

    @InjectMock
    CartaoRepository cartaoDeCreditoRepository;

    @InjectMock
    CartaoDeCreditoService cartaoDeCreditoService;

    @Inject
    CriarCartaoUseCase criarCartaoUseCase;

    @Tag("integra")
    @Test
    public void testCriarCartaoDeCreditoValidocomCliente(){

        //Arrange
        UUID clienteId = UUID.randomUUID();
        Customer customer = new Customer("João Silva", "1234567890");
        customer.setId(clienteId);

        // Mock do cliente
        when(customerRepository.findById(clienteId)).thenReturn(customer);

        // Mock do cartão retornado pelo service
        UUID cartaoId = UUID.randomUUID();
        CreditCard cartaoMock = new CreditCard(
                "1234567890123456",
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"));
        cartaoMock.setId(UUID.randomUUID());

        System.out.println("Recebido bandeira Mock: " + cartaoMock.getBandeira());

        // Mock do service
        when(cartaoDeCreditoService.criarCartao(
                eq("Mastercard"),
                eq("João Silva"),
                argThat(date -> date.isAfter(LocalDate.now().plusYears(4))),
                eq("123"),
                eq( new BigDecimal("1000.00")),
                eq( new BigDecimal("1000.00")),
                eq(customer))).thenReturn(cartaoMock);

        // Mock Repository
        when(cartaoDeCreditoRepository.save(any(CreditCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Preparar DTO de entrada
        CreditCardDTO dto = new CreditCardDTO();
        dto.setClienteId(customer.getId().toString());
        dto.setBandeira("Mastercard");
        dto.setNomeTitular(customer.getName());
        dto.setCvv("123");

        //Act
        CreditCard resultado = criarCartaoUseCase.executar(dto);

        //Assert
        assertNotNull(resultado);
        assertEquals("Mastercard", resultado.getBandeira());
        verify(customerRepository, times(1)).findById(clienteId);
        verify(cartaoDeCreditoService, times(1))
                .criarCartao(
                    eq("Mastercard"),
                    eq("João Silva"),
                    argThat(date -> date.isAfter(LocalDate.now().plusYears(4))),
                    eq("123"),
                    eq( new BigDecimal("1000.00")),
                    eq( new BigDecimal("1000.00")),
                    eq(customer));
        verify(cartaoDeCreditoRepository, times(1)).save(any(CreditCard.class));
    }
}



