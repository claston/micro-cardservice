package com.sistema.adaptadores.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class TransacaoResourceTest {

    @Tag("e2et")
    @Test
    public void testCriarTransacaoComCartaoDeCreditoeClienteExistentes(){
        // Passo 1: Crie um cliente
        String clienteId = criarCliente("João Silva", "12345678900", "joao.silva@email.com", "11999999999");

        // Passo 2: Crie um cartão com um número válido
        String cartaoId = criarCartao("1234567890123456", "João Silva", clienteId);

        // Passo 3: Valide a transação vinculada ao cartão criado
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

    private String criarCliente(String nome, String cpf, String email, String telefone){

        return RestAssured.given()
                .contentType("application/json")
                .body("{\"nome\":\"" + nome + "\",\"cpf\":\"" + cpf + "\",\"email\":\"" + "\",\"telefone\":\"" + telefone + "\"}")
                .post("/clientes")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

    }

    private String criarCartao(String numero, String nomeTitular, String clienteId){

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
