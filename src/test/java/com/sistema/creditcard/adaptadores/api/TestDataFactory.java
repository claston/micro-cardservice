package com.sistema.creditcard.adaptadores.api;

import io.restassured.RestAssured;

public class TestDataFactory {
    private static final String API_KEY = "key-dev";

    public static String criarCliente(String nome, String cpf, String email, String telefone) {
        String resolvedCpf = uniqueCpf();
        return RestAssured.given()
                .header("X-API-Key", API_KEY)
                .contentType("application/json")
                .body("{\"type\":\"INDIVIDUAL\",\"name\":\"" + nome + "\",\"documentType\":\"CPF\",\"documentNumber\":\"" + resolvedCpf + "\"}")
                .post("/customers")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    public static String criarCartao(String numero, String nomeTitular, String clienteId) {
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

