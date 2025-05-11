package com.sistema.dominio.entidade;

import com.sistema.util.CartaoDeCreditoBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FaturaTest {


    @Test
    public void testCalculoTotalEPagamentoMinimo(){

        LocalDate mesAno = LocalDate.of(2024,1,1);
        Fatura fatura = new Fatura(mesAno);

        //CartaoDeCredito cartao = CartaoDeCreditoTestFactory.criaCartaoValido();
        CartaoDeCredito cartao = new CartaoDeCreditoBuilder().build();

        LocalDateTime dataTransacao = LocalDateTime.of(2024,1,10, 10,0);

        Transacao t1 = new Transacao("Compra 1", new BigDecimal("100.00"),cartao, dataTransacao);
        Transacao t2 = new Transacao("Compra 2", new BigDecimal("50.00"),cartao, dataTransacao);

        fatura.addTransacao(t1);
        fatura.addTransacao(t2);

        fatura.calculaTotais();

        assertEquals(new BigDecimal("150.00"), fatura.getTotal());
        assertEquals(new BigDecimal("22.50"), fatura.getPagamentoMinimo());

    }

    //Poderia refatorar a criação do cartão trocando a factory pelo builder
    // Fazer um metodo para pagar a fatura

    @Test
    public void testPagamentoCheioDaFatura(){

        //Arrange:
        // cria uma fatura
        LocalDate mesAno = LocalDate.of(2025,2,1);
        Fatura fatura = new Fatura(mesAno);
        // adiciona 2 transações

        CartaoDeCredito cartao = new CartaoDeCreditoBuilder().build();

        LocalDateTime dataTransacao = LocalDateTime.of(2024,1,10, 10,0);

        Transacao t1 = new Transacao("Compra 1", new BigDecimal("100.00"),cartao, dataTransacao);
        Transacao t2 = new Transacao("Compra 2", new BigDecimal("50.00"),cartao, dataTransacao);

        fatura.addTransacao(t1);
        fatura.addTransacao(t2);

        // calcula os totais
        fatura.calculaTotais();

        assertEquals(new BigDecimal("150.00"), fatura.getTotal());
        assertEquals(new BigDecimal("22.50"), fatura.getPagamentoMinimo());
        assertEquals(new BigDecimal("150.00"), fatura.getValorEmAberto());

        // realizar o pagamento
        fatura.pagar(new BigDecimal("150.00"));
        // recalcula os totais
        fatura.calculaTotais();

        // Assert
        assertEquals(new BigDecimal("150.00"), fatura.getTotal());
        assertEquals(new BigDecimal("22.50"), fatura.getPagamentoMinimo());
        assertEquals(BigDecimal.ZERO, fatura.getValorEmAberto());
        assertTrue(fatura.isPaga());
    }
}