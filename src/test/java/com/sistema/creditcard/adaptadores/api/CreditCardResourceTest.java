package com.sistema.creditcard.adaptadores.api;

import com.sistema.creditcard.dominio.servico.CartaoDeCreditoService;
import com.sistema.creditcard.dominio.servico.GeradorNumeroCartao;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CreditCardResourceTest {
    private static final String API_KEY = "key-dev";

    @InjectMock
    GeradorNumeroCartao geradorNumeroCartao;

    @Inject
    CartaoDeCreditoService cartaoDeCreditoService;

    @Tag("e2e")
    @Test
    public void testCartaoDeCreditoComClienteExistente() {
        when(geradorNumeroCartao.gerarNumero()).thenReturn("1234567890123456");

        String clienteId = criarCliente("Joao Silva");

        RestAssured.given()
                .contentType("application/json")
                .body("{\"numero\":\"1234567890123456\",\"bandeira\":\"Mastercard\",\"nomeTitular\":\"Joao Silva\",\"clienteId\":\"" + clienteId + "\"}")
                .post("/cartoes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("cliente.id", equalTo(clienteId))
                .body("numero", equalTo("1234567890123456"))
                .body("bandeira", equalTo("Mastercard"))
                .body("nomeTitular", equalTo("Joao Silva"));
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

    private static String uniqueCpf() {
        long value = Math.abs(System.nanoTime() % 100_000_000_000L);
        return String.format("%011d", value);
    }
}

