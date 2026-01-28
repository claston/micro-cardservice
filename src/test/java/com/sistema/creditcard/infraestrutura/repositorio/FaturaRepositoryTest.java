package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.creditcard.infraestrutura.entidade.FaturaEntity;
import com.sistema.creditcard.infraestrutura.entidade.TransacaoEntity;
import com.sistema.customer.infra.entity.CustomerEntity;
import com.sistema.customer.infra.repository.CustomerRepositoryAdapter;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FaturaRepositoryTest extends DbCleanIT {

    @Inject
    FaturaRepository faturaRepository;

    @Inject
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Inject
    CustomerRepositoryAdapter customerRepositoryAdapter;

    @Tag("integration-test")
    @Test
    @Transactional
    public void testFindByMesAno() {

        LocalDate mesAno = LocalDate.of(2024, 10, 1);
        FaturaEntity fatura = new FaturaEntity();
        fatura.setMesAno(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        faturaRepository.persist(fatura);
        Optional<FaturaEntity> result = faturaRepository.findByMesAno(mesAno);

        assertTrue(result.isPresent(), "A fatura deve ser encontrada pelo mês/ano informado");
        FaturaEntity faturaRecuperada = result.get();
        assertNotNull(faturaRecuperada.getId());

        assertEquals(new BigDecimal("500.00"), faturaRecuperada.getTotal(), "O total da Fatura deve ser 500.00");
        assertEquals(mesAno, faturaRecuperada.getMesAno(), "A data deve corresponder à data definida");
    }

    @Tag("repository-fatura-2")
    @Test
    @Transactional
    public void testNovaFaturaEntityComNovaTransacaoEntity() {
        LocalDate mesAno = LocalDate.of(2024, 12, 1);
        FaturaEntity fatura = new FaturaEntity();
        fatura.setMesAno(mesAno);
        fatura.setTotal(new BigDecimal("500.00"));
        fatura.setPagamentoMinimo(new BigDecimal("75.00"));
        fatura.setPaga(false);

        CustomerEntity cliente = new CustomerEntity();
        cliente.setId(UUID.randomUUID());
        cliente.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        cliente.setType("INDIVIDUAL");
        cliente.setName("Joao da Silva");
        cliente.setDocumentType("CPF");
        cliente.setDocumentNumber("22222222222");
        cliente.setStatus("ACTIVE");
        cliente.setCreatedAt(Instant.now());
        cliente.setUpdatedAt(Instant.now());
        customerRepositoryAdapter.persist(cliente);

        CartaoDeCreditoEntity cartao = new CartaoDeCreditoEntity();
        cartao.setBandeira("MasterCard");
        cartao.setDataValidade(LocalDate.now().plusYears(5));
        cartao.setNumero("1234567890");
        cartao.setNomeTitular("Joao da Silva");
        cartao.setLimiteTotal(new BigDecimal("100.00"));
        cartao.setLimiteDisponivel(new BigDecimal("100.00"));
        cartao.setCvv("123");
        cartao.setBloqueadoPorPerdaOuRoubo(false);
        cartao.setCliente(cliente);
        cartaoDeCreditoRepository.persist(cartao);

        TransacaoEntity transacao = new TransacaoEntity();
        transacao.setDescricao("Compra Supermercado");
        transacao.setValor(new BigDecimal("100.00"));
        transacao.setDataHora(LocalDateTime.now());
        transacao.setCartao(cartao);

        fatura.addTransacao(transacao);

        faturaRepository.persist(fatura);
        Optional<FaturaEntity> result = faturaRepository.findByMesAno(mesAno);

        assertTrue(result.isPresent(), "A fatura deve ser encontrada pelo mês/ano informado");
        FaturaEntity faturaRecuperada = result.get();
        assertNotNull(faturaRecuperada.getId());
        assertEquals(new BigDecimal("500.00"), faturaRecuperada.getTotal(), "O total da Fatura deve ser 500.00");
        assertEquals(mesAno, faturaRecuperada.getMesAno(), "A data deve corresponder à data definida");

        assertEquals(1, faturaRecuperada.getTransacoes().size());
        assertNotNull(faturaRecuperada.getTransacoes().get(0).getId());
        assertNotNull(faturaRecuperada.getTransacoes().get(0).getFatura());
    }
}

