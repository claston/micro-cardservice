package com.sistema.creditcard.dominio.servico;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.customer.dominio.entidade.Customer;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CreditCardServiceTest {

    @InjectMock
    GeradorNumeroCartao geradorNumeroCartao;

    @Inject
    private CartaoDeCreditoService cartaoDeCreditoService;

    @Tag("unit-service")
    @Test
    public void testCriarCartaoComSucesso(){
        //MOcka o número do cartão
        when(geradorNumeroCartao.gerarNumero()).thenReturn("1111222233334444");

        // Configura o cliente
        Customer customer = new Customer("João Silva", "12345678990");
        CreditCard creditCard = cartaoDeCreditoService.criarCartao(
                "MasterCard",
                "João Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                customer);

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

        Customer customer = new Customer("João Silva", "12345678990");

        // Valida o lançamento de exceção
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cartaoDeCreditoService.criarCartao(
                        "MasterCard",
                        "João Silva",
                        LocalDate.now().plusYears(5),
                        "123",
                        BigDecimal.ZERO,
                        new BigDecimal("1000.00"),
                        customer)
        );

        assertEquals("O limite deve ser maior que zero.", exception.getMessage());
    }
}



