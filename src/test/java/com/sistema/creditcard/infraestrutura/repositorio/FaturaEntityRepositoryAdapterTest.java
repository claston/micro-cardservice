package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.dominio.entidade.Transacao;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import com.sistema.creditcard.dominio.repository.FaturaRepository;
import com.sistema.creditcard.util.CartaoDeCreditoBuilder;
import com.sistema.customer.dominio.entidade.Customer;
import com.sistema.infraestrutura.repositorio.DbCleanIT;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
@QuarkusTest
class FaturaEntityRepositoryAdapterTest extends DbCleanIT{

    @Inject
    FaturaRepository faturaRepository;

    @Inject
    CartaoRepository cartaoRepository;

    @Inject
    CartaoDeCreditoBuilder cartaoDeCreditoBuilder;

    @Tag("integration-test")
    @Test
    @Transactional
    public void testPersistNewFatura() {

        //Arrange:
        LocalDate mesAno = LocalDate.of(2025, 1, 1);

        // Verifica que não existe fatura para esse período:
        Optional<Fatura> optFatura = faturaRepository.findByMesAno(mesAno);
        assertTrue(optFatura.isEmpty());

        Fatura fatura = new Fatura(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        //Pagto minimo 15 porcento do total
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        //Act:
        Fatura faturaSalva = faturaRepository.save(fatura);

        //Assert:
        assertNotNull(faturaSalva);
        assertNotNull(faturaSalva.getId());
        assertEquals(mesAno, faturaSalva.getMesAno(), "O MesAno deve ser igual");
        assertEquals(new BigDecimal("500.00"), faturaSalva.getTotal(), "O valor total deve ser igual");
        assertEquals(new BigDecimal("75.00"), faturaSalva.getPagamentoMinimo());
        assertFalse(faturaSalva.isPaga());
    }
    
    @Tag("r-f-adapter-2")
    @Test
    @Transactional
    public void testPersistNewFaturaComTransacao() {

        //Arrange:
        LocalDate mesAno = LocalDate.of(2025, 2, 1);

        // Verifica que não existe fatura para esse período:
        Optional<Fatura> optFatura = faturaRepository.findByMesAno(mesAno);
        assertTrue(optFatura.isEmpty());

        Fatura fatura = new Fatura(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        //Pagto minimo 15 porcento do total
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        // Cria um cliente para o cartão de crédito
        Customer customer = new Customer();
        customer.setName("João Silva");

        CreditCard cartao = cartaoDeCreditoBuilder
                .comCliente(customer)
                .persistindoCliente()
                .build();

        //Cria cartão de crédito para a transacao
        CreditCard cartaoSalvo = cartaoRepository.save(cartao);

        // Cria a transação
        Transacao transacao = new Transacao(
                "Compra Supermercado",
                new BigDecimal("100.00"), cartaoSalvo, LocalDateTime.now());

        //Adiciona a transacao na fatura
        fatura.addTransacao(transacao);

        //Act:
        Fatura faturaSalva = faturaRepository.save(fatura);

        //Assert:
        assertNotNull(faturaSalva);
        assertNotNull(faturaSalva.getId());
        assertEquals(mesAno, faturaSalva.getMesAno(), "O MesAno deve ser igual");
        assertEquals(new BigDecimal("500.00"), faturaSalva.getTotal(), "O valor total deve ser igual");
        assertEquals(new BigDecimal("75.00"), faturaSalva.getPagamentoMinimo());
        assertFalse(faturaSalva.isPaga());
        assertEquals(1, fatura.getTransacoes().size(), "Deve haver somente 1 transação");
        assertNotNull(fatura.getTransacoes().get(0));
    }

    @Tag("integration-test")
    @Test
    @Transactional
    public void testPersistPagamentoDeFatura() {
        //Arrange:
        LocalDate mesAno = LocalDate.of(2022, 1, 1);

        // Verifica que não existe fatura para esse período:
        Optional<Fatura> optFatura = faturaRepository.findByMesAno(mesAno);
        assertTrue(optFatura.isEmpty());

        Fatura fatura = new Fatura(mesAno);
        //fatura.setTotal(new BigDecimal("500.00"));
        //Pagto minimo 15 porcento do total
        //fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        // Cria um cliente para o cartão de crédito
        Customer customer = new Customer();
        customer.setName("João Silva");

        CreditCard cartao = cartaoDeCreditoBuilder
                .comCliente(customer)
                .persistindoCliente()
                .build();

        //Cria cartão de crédito para a transacao
        CreditCard cartaoSalvo = cartaoRepository.save(cartao);

        // Cria a transação
        Transacao transacao = new Transacao(
                "Compra Supermercado",
                new BigDecimal("100.00"), cartaoSalvo, LocalDateTime.now());

        //Adiciona a transacao na fatura
        fatura.addTransacao(transacao);

        // Calcula os totais: Não usou o serviço
        fatura.calculaTotais();

        assertEquals(new BigDecimal("100.00"), fatura.getTotal());

        //Salva a Fatura
        Fatura faturaSalva = faturaRepository.save(fatura);

        //Assert:
        assertNotNull(faturaSalva);
        assertNotNull(faturaSalva.getId());
        assertEquals(mesAno, faturaSalva.getMesAno(), "O MesAno deve ser igual");
        assertEquals(new BigDecimal("100.00"), faturaSalva.getTotal(), "O valor total deve ser igual");
        assertEquals(new BigDecimal("15.00"), faturaSalva.getPagamentoMinimo());
        assertEquals(new BigDecimal("100.00"), faturaSalva.getValorEmAberto());
        assertFalse(faturaSalva.isPaga());

        assertEquals(1, fatura.getTransacoes().size(), "Deve haver somente 1 transação");
        assertNotNull(fatura.getTransacoes().get(0));

        //Act: Paga a fatura
        fatura.pagar(new BigDecimal("100.00"));
        fatura.calculaTotais();

        assertEquals(BigDecimal.ZERO, fatura.getValorEmAberto());

        Fatura faturaPagaSalva = faturaRepository.save(fatura);

        faturaPagaSalva.calculaTotais();

        assertEquals(BigDecimal.ZERO, faturaPagaSalva.getValorEmAberto());

        //Assert:
        assertNotNull(faturaPagaSalva);
        assertNotNull(faturaPagaSalva.getId());
        assertEquals(mesAno, faturaPagaSalva.getMesAno(), "O MesAno deve ser igual");
        assertEquals(new BigDecimal("100.00"), faturaPagaSalva.getTotal(), "O valor total deve ser igual");
        assertEquals(new BigDecimal("15.00"), faturaPagaSalva.getPagamentoMinimo());

        assertEquals(1, fatura.getTransacoes().size(), "Deve haver somente 1 transação");
        assertNotNull(fatura.getTransacoes().get(0));
    }
}




