package com.sistema.infraestrutura.repositorio;

import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.CustomerEntity;
import com.sistema.infraestrutura.entidade.TransacaoEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TransacaoRepositoryTest extends DbCleanIT {

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Inject
    TransacaoRepository transacaoRepository;

    @Tag("integration-test")
    @Test
    @Transactional
    public void testDeveRegistarCompraComSucesso(){

        //criar um cartão de crédito com um cliente

        // criar um cliente entity
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName("João Silva");
        clienteRepository.persist(customerEntity);

        //criar um cartao de credito entity
        CartaoDeCreditoEntity cartaoEntity = new CartaoDeCreditoEntity();
        //cartaoEntity.setId(UUID.randomUUID());
        cartaoEntity.setBandeira("Mastercard");
        cartaoEntity.setNumero("1111222233334444");
        cartaoEntity.setNomeTitular("João Silva");
        cartaoEntity.setLimiteTotal(new BigDecimal("1000.00"));
        cartaoEntity.setLimiteDisponivel(new BigDecimal("1000.00"));
        cartaoEntity.setDataValidade(LocalDate.now().plusYears(5));
        cartaoEntity.setCliente(customerEntity);

        // chamar o repository para persistir
        cartaoDeCreditoRepository.persist(cartaoEntity);

        // criar a transação
        TransacaoEntity transacaoEntity = new TransacaoEntity();
        transacaoEntity.setCartao(cartaoEntity);
        transacaoEntity.setDescricao("Compra de comida");
        transacaoEntity.setDataHora(LocalDateTime.now());
        transacaoEntity.setValor(new BigDecimal("252.12"));

        transacaoRepository.persist(transacaoEntity);

        // Recuperar Transação

        TransacaoEntity transacaoPersistida = transacaoRepository.findById(transacaoEntity.getId());

        assertNotNull(transacaoPersistida);
        assertEquals(transacaoEntity.getId(), transacaoPersistida.getId());
        assertEquals("Compra de comida", transacaoPersistida.getDescricao());
        assertEquals(new BigDecimal("252.12"), transacaoPersistida.getValor());
        assertNotNull(transacaoPersistida.getDataHora());

    }

}
