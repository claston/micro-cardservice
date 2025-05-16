package com.sistema.infraestrutura.repositorio;

import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.entidade.CustomerEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CartaoDeCreditoRepositoryTest {

    @Inject
    CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Tag("repository-cartao")
    @Test
    @Transactional
    public void testCriarCartaoDeCreditoValidoNaBaseComCliente(){

        // criar um cliente entity
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setNome("João Silva");
        customerEntity.setAtivo(true);
        customerEntity.setDataCadastro(LocalDate.now());
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

        // verificar o resultado
        assertNotNull(cartaoEntity);
        assertNotNull(cartaoEntity.getCliente().getDataCadastro());
        assertTrue((cartaoEntity.getCliente().isAtivo()));
        assertEquals("Mastercard", cartaoEntity.getBandeira());
        assertEquals("João Silva", cartaoEntity.getNomeTitular());
        assertEquals("1111222233334444", cartaoEntity.getNumero());
        assertEquals(new BigDecimal("1000.00"), cartaoEntity.getLimiteTotal());
        assertEquals(new BigDecimal("1000.00"), cartaoEntity.getLimiteDisponivel());
    }
}