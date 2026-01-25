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

    @InjectMock
    GeradorNumeroCartao geradorNumeroCartao;

    @Inject
    CartaoDeCreditoService cartaoDeCreditoService;

    @Tag("e2e")
    @Test
    public void testCartaoDeCreditoComClienteExistente(){

       when(geradorNumeroCartao.gerarNumero()).thenReturn("1234567890123456");

        // Passo 1: Crie um cliente
        String clienteId = criarCliente("João Silva", "12345678900", "joao.silva@email.com", "11999999999");

        RestAssured.given()
                .contentType("application/json")
                .body("{\"numero\":\"1234567890123456\",\"bandeira\":\"Mastercard\",\"nomeTitular\":\"João Silva\",\"clienteId\":\"" + clienteId + "\"}")
                .post("/cartoes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("cliente.id", equalTo(clienteId))
                .body("numero", equalTo("1234567890123456"))
                .body("bandeira", equalTo("Mastercard"))
                .body("nomeTitular", equalTo("João Silva"));

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
}


