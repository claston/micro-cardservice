package com.sistema.creditcard.adaptadores.api;

import com.sistema.creditcard.adaptadores.dto.TransacaoDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class FaturaResourceTest {

    @Tag("e2et-fatura")
    @Test
    /*
     * Objetivo é testar se o sistema é capaz adicionar uma transação válida em uma fatura.
     */
    public void testAdicionaTransacaoNaFaturaComValorEDataValida(){

        //Passo 1 - Teria de pegar a transação e adicionar na fatura do mês, para isso teria que verificar se existe alguma
        // fatura, isso deveria ser feito no serviço de fatura.

        String clienteId = TestDataFactory.criarCliente("João Silva", "12345678900", "joao.silva@email.com", "11999999999");
        String cartaoId = TestDataFactory.criarCartao("1234567890123456", "João Silva", clienteId);

        // Criar um JSON simulando uma nova transação.
        final TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setValor(new BigDecimal("150.00"));
        transacaoDTO.setDescricao("Compra 3");
        transacaoDTO.setDataHora(LocalDateTime.of(2024,1,2, 10,20));
        transacaoDTO.setCartaoId(UUID.fromString(cartaoId));

        RestAssured.config = newConfig().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));

        RestAssured.given()
                .contentType("application/json")
                .body(transacaoDTO)
                .post("/fatura/transacao")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("transacoes[0].cartao.id", equalTo(cartaoId))
                .body("transacoes[0].descricao", equalTo("Compra 3"))
                .body("transacoes[0].valor", comparesEqualTo(new BigDecimal("150.00")));

    }
}


