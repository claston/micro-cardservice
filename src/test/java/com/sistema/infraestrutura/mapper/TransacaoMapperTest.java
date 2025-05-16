package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.TransacaoDTO;
import com.sistema.dominio.entidade.CreditCard;
import com.sistema.dominio.entidade.Transacao;
import com.sistema.util.CartaoDeCreditoBuilder;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TransacaoMapperTest {

    @Inject
    TransacaoMapper transacaoMapper;

    @Inject
    CartaoDeCreditoBuilder cartaoDeCreditoBuilder;

    @Tag("mapper-transacao-testDomainToDTO")
    @Test
    public void testDomainToDTO(){
        // Arrange

        String descricao = "Compra com cartão";
        BigDecimal valor = new BigDecimal("150.00");
        LocalDateTime dataHora = LocalDateTime.of(2024, 1, 10,  10, 0);
        UUID cartaoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        CreditCard cartao = cartaoDeCreditoBuilder.comID(cartaoId).build();

        UUID transacaoId = UUID.fromString("111e4567-e89b-12d3-a456-426614174000");
        Transacao transacao = new Transacao(descricao, valor, cartao, dataHora);
        transacao.setId(transacaoId);

        //Act
        TransacaoDTO dto = transacaoMapper.toDTO(transacao);

        //Assert
        assertNotNull(dto, "O Dto não deve ser nulo");
        assertEquals(descricao, dto.getDescricao(), "A descrição deve ser igual");
        assertEquals(valor, dto.getValor(), "O valor deve ser igual");
        assertEquals(dataHora, dto.getDataHora(), "A data/hora deve ser igual");
        assertNotNull(dto.getCartaoId(), "O cartaoId não deve ser nulo");
        assertEquals(cartaoId, dto.getCartaoId(), "O cartaoId deve ser igual");
    }
}