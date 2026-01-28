package com.sistema.creditcard.casodeuso;

import com.sistema.creditcard.adaptadores.dto.CreditCardDTO;
import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import com.sistema.creditcard.dominio.servico.CartaoDeCreditoService;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerStatus;
import com.sistema.customer.domain.model.CustomerType;
import com.sistema.customer.domain.repository.CustomerRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CriarCartaoUseCaseTest {
    private static final UUID DEFAULT_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

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
    public void testCriarCartaoDeCreditoValidocomCliente() {
        UUID clienteId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(clienteId);
        customer.setTenantId(DEFAULT_TENANT_ID);
        customer.setType(CustomerType.INDIVIDUAL);
        customer.setName("Joao Silva");
        customer.setDocumentType(CustomerDocumentType.CPF);
        customer.setDocumentNumber("12345678901");
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());

        when(customerRepository.findById(DEFAULT_TENANT_ID, clienteId)).thenReturn(Optional.of(customer));

        CreditCard cartaoMock = new CreditCard(
                "1234567890123456",
                "Mastercard",
                "Joao da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00")
        );
        cartaoMock.setId(UUID.randomUUID());

        when(cartaoDeCreditoService.criarCartao(
                eq("Mastercard"),
                eq("Joao Silva"),
                argThat(date -> date.isAfter(LocalDate.now().plusYears(4))),
                eq("123"),
                eq(new BigDecimal("1000.00")),
                eq(new BigDecimal("1000.00")),
                eq(customer)
        )).thenReturn(cartaoMock);

        when(cartaoDeCreditoRepository.save(any(CreditCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreditCardDTO dto = new CreditCardDTO();
        dto.setClienteId(customer.getId().toString());
        dto.setBandeira("Mastercard");
        dto.setNomeTitular(customer.getName());
        dto.setCvv("123");

        CreditCard resultado = criarCartaoUseCase.executar(dto);

        assertNotNull(resultado);
        assertEquals("Mastercard", resultado.getBandeira());
        verify(customerRepository, times(1)).findById(DEFAULT_TENANT_ID, clienteId);
        verify(cartaoDeCreditoService, times(1)).criarCartao(
                eq("Mastercard"),
                eq("Joao Silva"),
                argThat(date -> date.isAfter(LocalDate.now().plusYears(4))),
                eq("123"),
                eq(new BigDecimal("1000.00")),
                eq(new BigDecimal("1000.00")),
                eq(customer)
        );
        verify(cartaoDeCreditoRepository, times(1)).save(any(CreditCard.class));
    }
}

