package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.dominio.entidade.Transacao;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import com.sistema.creditcard.dominio.repository.FaturaRepository;
import com.sistema.creditcard.util.CartaoDeCreditoBuilder;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerStatus;
import com.sistema.customer.domain.model.CustomerType;
import com.sistema.infraestrutura.repositorio.DbCleanIT;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FaturaEntityRepositoryAdapterTest extends DbCleanIT {

    @Inject
    FaturaRepository faturaRepository;

    @Inject
    CartaoRepository cartaoRepository;

    @Inject
    CartaoDeCreditoBuilder cartaoDeCreditoBuilder;

    private static Customer validCustomer() {
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

    @Tag("integration-test")
    @Test
    @Transactional
    public void testPersistNewFatura() {
        LocalDate mesAno = LocalDate.of(2025, 1, 1);

        Optional<Fatura> optFatura = faturaRepository.findByMesAno(mesAno);
        assertTrue(optFatura.isEmpty());

        Fatura fatura = new Fatura(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        Fatura faturaSalva = faturaRepository.save(fatura);

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
        LocalDate mesAno = LocalDate.of(2025, 2, 1);

        Optional<Fatura> optFatura = faturaRepository.findByMesAno(mesAno);
        assertTrue(optFatura.isEmpty());

        Fatura fatura = new Fatura(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        CreditCard cartao = cartaoDeCreditoBuilder
                .comCliente(validCustomer())
                .persistindoCliente()
                .build();

        CreditCard cartaoSalvo = cartaoRepository.save(cartao);

        Transacao transacao = new Transacao(
                "Compra Supermercado",
                new BigDecimal("100.00"), cartaoSalvo, LocalDateTime.now());

        fatura.addTransacao(transacao);

        Fatura faturaSalva = faturaRepository.save(fatura);

        assertNotNull(faturaSalva);
        assertNotNull(faturaSalva.getId());
        assertEquals(mesAno, faturaSalva.getMesAno(), "O MesAno deve ser igual");
        assertEquals(new BigDecimal("500.00"), faturaSalva.getTotal(), "O valor total deve ser igual");
        assertEquals(new BigDecimal("75.00"), faturaSalva.getPagamentoMinimo());
        assertFalse(faturaSalva.isPaga());
        assertEquals(1, fatura.getTransacoes().size(), "Deve haver somente 1 transacao");
        assertNotNull(fatura.getTransacoes().get(0));
    }

    @Tag("integration-test")
    @Test
    @Transactional
    public void testPersistPagamentoDeFatura() {
        LocalDate mesAno = LocalDate.of(2022, 1, 1);

        Optional<Fatura> optFatura = faturaRepository.findByMesAno(mesAno);
        assertTrue(optFatura.isEmpty());

        Fatura fatura = new Fatura(mesAno);
        fatura.setPaga(false);

        CreditCard cartao = cartaoDeCreditoBuilder
                .comCliente(validCustomer())
                .persistindoCliente()
                .build();

        CreditCard cartaoSalvo = cartaoRepository.save(cartao);

        Transacao transacao = new Transacao(
                "Compra Supermercado",
                new BigDecimal("100.00"), cartaoSalvo, LocalDateTime.now());

        fatura.addTransacao(transacao);

        fatura.calculaTotais();
        assertEquals(new BigDecimal("100.00"), fatura.getTotal());

        Fatura faturaSalva = faturaRepository.save(fatura);

        assertNotNull(faturaSalva);
        assertNotNull(faturaSalva.getId());
        assertEquals(mesAno, faturaSalva.getMesAno(), "O MesAno deve ser igual");
        assertEquals(new BigDecimal("100.00"), faturaSalva.getTotal(), "O valor total deve ser igual");
        assertEquals(new BigDecimal("15.00"), faturaSalva.getPagamentoMinimo());
        assertEquals(new BigDecimal("100.00"), faturaSalva.getValorEmAberto());
        assertFalse(faturaSalva.isPaga());

        fatura.pagar(new BigDecimal("100.00"));
        fatura.calculaTotais();
        assertEquals(BigDecimal.ZERO, fatura.getValorEmAberto());

        Fatura faturaPagaSalva = faturaRepository.save(fatura);
        faturaPagaSalva.calculaTotais();
        assertEquals(BigDecimal.ZERO, faturaPagaSalva.getValorEmAberto());
        assertNotNull(faturaPagaSalva.getId());
    }
}

