package com.sistema.creditcard.dominio.entidade;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransacaoDomainTest {

    @Test
    public void testTransacaoValida(){
        System.out.println("=== Teste: Criação de transação válida ===");

        CreditCard cartao = CartaoDeCreditoTestFactory.criaCartaoValido();


        Transacao transacao = new Transacao(
                "Compra Supermercado",
                new BigDecimal("100.00"), cartao, LocalDateTime.now());

        assertNotNull(transacao);
        // Se a transacao não foi persistida, ela não tem ID.
        assertEquals("Compra Supermercado", transacao.getDescricao());
        assertEquals(new BigDecimal("100.00"), transacao.getValor());
        assertNotNull(transacao.getDataHora());
        assertEquals(cartao, transacao.getCartao());
    }

    @Test
    public void testCriarTransacaoComValorNegativo(){
        System.out.println("=== Teste: Criação de Transação com valor negativo ===");
        CreditCard cartao = CartaoDeCreditoTestFactory.criaCartaoValido();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Transacao(
                        "Compra Supermercado",
                        new BigDecimal("-100.00"), cartao, LocalDateTime.now()));

        assertEquals("O valor da transação deve ser maior que zero.", exception.getMessage());

    }

    @Test
    public void testCriarTransacaoComDescricaoNula(){
        System.out.println("=== Teste: Criar transação com descrição nula ===");
        CreditCard cartao = CartaoDeCreditoTestFactory.criaCartaoValido();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Transacao(
                        null,
                        new BigDecimal("100.00"), cartao, LocalDateTime.now()));

        assertEquals("Descrição da transação não pode ser nula ou vazia", exception.getMessage());

    }

    @Test
    public void testCriarTransacaoComCartaoNulo() {
        System.out.println("\n=== Teste: Criação de Transação com Cartão Nulo ===");
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Transacao("Compra supermercado", new BigDecimal("100.00"), null, LocalDateTime.now()));

        assertEquals("Cartão não pode ser nulo.", exception.getMessage());
    }
}



