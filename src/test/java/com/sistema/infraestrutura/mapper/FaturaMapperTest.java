package com.sistema.infraestrutura.mapper;

import com.sistema.dominio.entidade.Fatura;
import com.sistema.infraestrutura.entidade.FaturaEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class FaturaMapperTest {

    //@Inject
    //FaturaEntity faturaEntity;

    @Inject
    FaturaMapper faturaMapper;

    @Tag("mapper-fatura-testEntityToDomain")
    @Test
    public void testEntityToDomain(){

        UUID faturaID = UUID.randomUUID();
        BigDecimal totalFatura = new BigDecimal("100.00");
        BigDecimal valorMinimo = new BigDecimal("15.00");
        boolean faturaPaga = false;

        //Arrange:
        FaturaEntity faturaEntity = new FaturaEntity();
        faturaEntity.setId(faturaID);
        faturaEntity.setPaga(faturaPaga);
        faturaEntity.setTotal(totalFatura);
        faturaEntity.setPagamentoMinimo(new BigDecimal("15.00"));
        //TODO: Verificar esse mais tarde: faturaEntity.setTransacoes();

        //Act:
        Fatura fatura = faturaMapper.toDomain(faturaEntity);

        //Assert:
        assertNotNull(fatura);
        assertEquals(faturaID, fatura.getId(), "O Id deve ser igual");
        assertEquals(totalFatura, fatura.getTotal(), "O valor total deve ser igual");
        assertEquals(valorMinimo, fatura.getPagamentoMinimo(), "O valor mínimo deve ser igual");
        assertFalse(fatura.isPaga(), "O pagamento da fatura deve ser false");

    }
}
