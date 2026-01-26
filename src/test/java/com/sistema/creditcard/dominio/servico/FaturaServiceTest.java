package com.sistema.creditcard.dominio.servico;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.entidade.CartaoDeCreditoTestFactory;
import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.dominio.entidade.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FaturaServiceTest {

    private FaturaService faturaService;
    private Fatura fatura;
    private CreditCard creditCard;

    @BeforeEach
    public void setup(){
        faturaService = new FaturaService();
        fatura = new Fatura(LocalDate.of(2024,1, 1));
    }

    @Tag("service-fatura")
    @Test
    public void testAdicionarTransacaoAtualizaTotalEPagamentoMinimo() {
        //Arrange: Cria 2 transações para o mesmo período
        creditCard = CartaoDeCreditoTestFactory.criaCartaoValido();
        LocalDateTime dataTransacao = LocalDateTime.of(2024, 1, 10, 10, 0);
        Transacao t1 = new Transacao("Compra 1", new BigDecimal("100.00"), creditCard,dataTransacao);
        Transacao t2 = new Transacao( "Compra 2", new BigDecimal("50.00"), creditCard, dataTransacao);

        faturaService.adicionarTransacao(fatura, t1);
        faturaService.adicionarTransacao(fatura, t2);

        // Assert: Verifica se o total e o pagamento mínimos foram calculadores corretamente
        BigDecimal totalEsperado = new BigDecimal("150.00");
        BigDecimal pagamentoMinimoEsperado = totalEsperado.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);

        assertEquals(totalEsperado, fatura.getTotal(), "o total deve ser 150.00");
        assertEquals(pagamentoMinimoEsperado, fatura.getPagamentoMinimo(), "o pagamento mínimo deve ser 22.50");
    }
}


