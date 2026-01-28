package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.infraestrutura.entidade.CartaoDeCreditoEntity;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class TransacaoRepositoryTest extends DbCleanIT {

    @Inject
    CustomerRepositoryAdapter customerRepositoryAdapter;

    @Inject
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Inject
    TransacaoRepository transacaoRepository;

    @Tag("integration-test")
    @Test
    @Transactional
    public void testDeveRegistarCompraComSucesso() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(UUID.randomUUID());
        customerEntity.setTenantId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        customerEntity.setType("INDIVIDUAL");
        customerEntity.setName("Joao Silva");
        customerEntity.setDocumentType("CPF");
        customerEntity.setDocumentNumber("12345678901");
        customerEntity.setStatus("ACTIVE");
        customerEntity.setCreatedAt(Instant.now());
        customerEntity.setUpdatedAt(Instant.now());
        customerRepositoryAdapter.persist(customerEntity);

        CartaoDeCreditoEntity cartaoEntity = new CartaoDeCreditoEntity();
        cartaoEntity.setBandeira("Mastercard");
        cartaoEntity.setNumero("1111222233334444");
        cartaoEntity.setNomeTitular("Joao Silva");
        cartaoEntity.setLimiteTotal(new BigDecimal("1000.00"));
        cartaoEntity.setLimiteDisponivel(new BigDecimal("1000.00"));
        cartaoEntity.setDataValidade(LocalDate.now().plusYears(5));
        cartaoEntity.setCliente(customerEntity);
        cartaoDeCreditoRepository.persist(cartaoEntity);

        TransacaoEntity transacaoEntity = new TransacaoEntity();
        transacaoEntity.setCartao(cartaoEntity);
        transacaoEntity.setDescricao("Compra de comida");
        transacaoEntity.setDataHora(LocalDateTime.now());
        transacaoEntity.setValor(new BigDecimal("252.12"));
        transacaoRepository.persist(transacaoEntity);

        TransacaoEntity transacaoPersistida = transacaoRepository.findById(transacaoEntity.getId());

        assertNotNull(transacaoPersistida);
        assertEquals(transacaoEntity.getId(), transacaoPersistida.getId());
        assertEquals("Compra de comida", transacaoPersistida.getDescricao());
        assertEquals(new BigDecimal("252.12"), transacaoPersistida.getValor());
        assertNotNull(transacaoPersistida.getDataHora());
    }
}

