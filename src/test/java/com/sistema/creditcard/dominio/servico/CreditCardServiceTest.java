package com.sistema.creditcard.dominio.servico;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerStatus;
import com.sistema.customer.domain.model.CustomerType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CreditCardServiceTest {

    @InjectMock
    GeradorNumeroCartao geradorNumeroCartao;

    @Inject
    private CartaoDeCreditoService cartaoDeCreditoService;

    private static Customer sampleCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        customer.setType(CustomerType.INDIVIDUAL);
        customer.setName("Joao Silva");
        customer.setDocumentType(CustomerDocumentType.CPF);
        customer.setDocumentNumber("12345678901");
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        return customer;
    }

    @Tag("unit-service")
    @Test
    public void testCriarCartaoComSucesso() {
        when(geradorNumeroCartao.gerarNumero()).thenReturn("1111222233334444");

        CreditCard creditCard = cartaoDeCreditoService.criarCartao(
                "MasterCard",
                "Joao Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                sampleCustomer()
        );

        assertNotNull(creditCard);
        assertEquals("1111222233334444", creditCard.getNumero());
        assertEquals("MasterCard", creditCard.getBandeira());
        assertEquals("123", creditCard.getCvv());
        assertNotNull(creditCard.getDataValidade());
        assertEquals(new BigDecimal("1000.00"), creditCard.getLimiteDisponivel());
        assertEquals(new BigDecimal("1000.00"), creditCard.getLimiteTotal());
    }

    @Tag("unit-service")
    @Test
    public void testCriarCartaoComLimiteInvalido() {
        when(geradorNumeroCartao.gerarNumero()).thenReturn("1111222233334444");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cartaoDeCreditoService.criarCartao(
                        "MasterCard",
                        "Joao Silva",
                        LocalDate.now().plusYears(5),
                        "123",
                        BigDecimal.ZERO,
                        new BigDecimal("1000.00"),
                        sampleCustomer()
                )
        );

        assertEquals("O limite deve ser maior que zero.", exception.getMessage());
    }
}

