package com.sistema.creditcard.adaptadores.api;

import io.restassured.RestAssured;

public class TestDataFactory {

    public static String criarCliente(String nome, String cpf, String email, String telefone){

        return RestAssured.given()
                .contentType("application/json")
                .body("{\"nome\":\"" + nome + "\",\"cpf\":\"" + cpf + "\",\"email\":\"" + "\",\"telefone\":\"" + telefone + "\"}")
                .post("/clientes")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

    }

    public static String criarCartao(String numero, String nomeTitular, String clienteId){

        return RestAssured.given()
                .contentType("application/json")
                .body("{\"numero\":\"1234567890123456\",\"bandeira\":\"Mastercard\",\"nomeTitular\":\"João Silva\",\"clienteId\":\"" + clienteId + "\"}")
                .post("/cartoes")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

}

