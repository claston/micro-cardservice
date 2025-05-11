package com.sistema.dominio.entidade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartaoDeCreditoDomainTest {

    @Test
    public void testCriarCartaoDeCredito(){
        System.out.println("=== Teste: Criação de Cartão de Crédito ====");

        UUID cartaoId = UUID.randomUUID();

        CartaoDeCredito cartao = new CartaoDeCredito(
                "1234567890123456",
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"));

        assertNotNull(cartao);
        assertEquals("Mastercard", cartao.getBandeira());
        assertEquals("João da Silva", cartao.getNomeTitular());
        assertEquals("1234567890123456", cartao.getNumero());
        assertEquals(new BigDecimal("1000.00"), cartao.getLimiteTotal());
        assertEquals(new BigDecimal("1000.00"), cartao.getLimiteDisponivel());
        assertEquals(BigDecimal.ZERO, cartao.getSaldoDevedor());

        System.out.println("Cartão criado com sucesso: " + cartao);

    }

    @Test
    public void testRealizarCompra(){
        System.out.println("=== Teste: Realizar Compra ===");

        UUID cartaoId = UUID.randomUUID();

        CartaoDeCredito cartaoDeCredito = new CartaoDeCredito(
                "1234567890123456",
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"));

        // Compra Aprovada
        boolean compraAprovada = cartaoDeCredito.realizarCompra(new BigDecimal("200.00"));
        assertTrue(compraAprovada);
        assertEquals(new BigDecimal("200.00"), cartaoDeCredito.getSaldoDevedor());
        assertEquals(new BigDecimal("800.00"), cartaoDeCredito.getLimiteDisponivel());

        // Compra Negada por saldo insuficiente
        boolean compraNegada = cartaoDeCredito.realizarCompra(new BigDecimal("1000"));
        assertFalse(compraNegada);
        assertEquals(new BigDecimal("200.00"), cartaoDeCredito.getSaldoDevedor());
        assertEquals(new BigDecimal("800.00"), cartaoDeCredito.getLimiteDisponivel());

        System.out.println("Compra teste realizada com sucesso!");
    }

    @Test
    public void testCartaoDadosInvalidos(){
        System.out.println("=== Teste: Criação de Cartão com Dados Inválidos");

        UUID cartaoId = UUID.randomUUID();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CartaoDeCredito(
                        "1234567890123456",
                        "Mastercard",
                        null,
                        LocalDate.now().plusYears(5),
                        "123",
                        new BigDecimal("1000.00"),
                        new BigDecimal("1000.00")));

        assertEquals("Nome do titular do cartão não pode ser nulo ou vazio", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
                new CartaoDeCredito(
                        "123",
                        "Mastercard",
                        "João da Silva",
                        LocalDate.now().plusYears(5),
                        "123",
                        new BigDecimal("1000.00"),
                        new BigDecimal("1000.00")));

        assertEquals("Número do cartão inválido", exception.getMessage());
    }

    @Test
    public void testCompraIgualaLimite(){
        System.out.println("=== Teste: Compra que Iguala o Limite ===");

        UUID cartaoId = UUID.randomUUID();

        CartaoDeCredito cartao = new CartaoDeCredito(
                "1234567890123456",
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"));

        boolean compraAprovada = cartao.realizarCompra(new BigDecimal("1000.00"));
        assertTrue(compraAprovada);
        assertEquals(new BigDecimal("1000.00"), cartao.getSaldoDevedor());
        assertEquals(new BigDecimal("0.00"), cartao.getLimiteDisponivel());
    }

    @Test
    public void testCompraValorZero(){
        System.out.println("=== Teste: Compra com valor zero ===");

        CartaoDeCredito cartao = CartaoDeCreditoTestFactory.criaCartaoValido();

        boolean compraNegada = cartao.realizarCompra(BigDecimal.ZERO);
        assertFalse(compraNegada);
    }

    @Test
    public void testPagamentoSaldo(){
        System.out.println("=== Teste: Pagamento de Saldo ===");

        UUID cartaoId = UUID.randomUUID();

        CartaoDeCredito cartao = new CartaoDeCredito(
                "1234567890123456",
                "Mastercard",
                "João da Silva",
                LocalDate.now().plusYears(5),
                "123",
                new BigDecimal("5000.00"),
                new BigDecimal("5000.00"));

        cartao.realizarCompra(new BigDecimal("1000.00"));
        cartao.realizarPagamento(new BigDecimal("500.00"));

        assertEquals(new BigDecimal("500.00"), cartao.getSaldoDevedor());
        assertEquals(new BigDecimal("4500.00"), cartao.getLimiteDisponivel());

    }
}


