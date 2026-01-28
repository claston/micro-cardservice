package com.sistema.creditcard.adaptadores.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class TransacaoResourceTest {
    private static final String API_KEY = "key-dev";

    @Tag("e2et")
    @Test
    public void testCriarTransacaoComCartaoDeCreditoeClienteExistentes() {
        String clienteId = criarCliente("Joao Silva");
        String cartaoId = criarCartao("1234567890123456", "Joao Silva", clienteId);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"descricao\":\"teste compra\",\"valor\":\"500\",\"cartaoId\":\"" + cartaoId + "\"}")
                .post("/transacoes")
                .then()
                .statusCode(201)
                .body("cartao.id", equalTo(cartaoId))
                .body("descricao", equalTo("teste compra"))
                .body("valor", equalTo(500));
    }

    private String criarCliente(String nome) {
        String cpf = uniqueCpf();
        return RestAssured.given()
                .header("X-API-Key", API_KEY)
                .contentType("application/json")
                .body("{\"type\":\"INDIVIDUAL\",\"name\":\"" + nome + "\",\"documentType\":\"CPF\",\"documentNumber\":\"" + cpf + "\"}")
                .post("/customers")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String criarCartao(String numero, String nomeTitular, String clienteId) {
        return RestAssured.given()
                .contentType("application/json")
                .body("{\"numero\":\"" + numero + "\",\"bandeira\":\"Mastercard\",\"nomeTitular\":\"" + nomeTitular + "\",\"clienteId\":\"" + clienteId + "\"}")
                .post("/cartoes")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private static String uniqueCpf() {
        long value = Math.abs(System.nanoTime() % 100_000_000_000L);
        return String.format("%011d", value);
    }
}

