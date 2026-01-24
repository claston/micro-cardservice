package com.sistema.adaptadores.api;

import com.sistema.adaptadores.dto.CustomerDTO;
import com.sistema.casodeuso.CriarClienteUseCase;
import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.repository.CustomerRepository;
import com.sistema.infraestrutura.mapper.CustomerMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CustomerResourceTest {

    @InjectMock
    CustomerRepository customerRepository;

    @InjectMock
    CustomerMapper customerMapper;

    @InjectMock
    CriarClienteUseCase criarClienteUseCase;

    @Test
    public void getAllClientesReturnsNoContentWhenEmpty() {
        when(customerRepository.findAllAsList()).thenReturn(List.of());

        RestAssured.given()
                .get("/clientes")
                .then()
                .statusCode(204);
    }

    @Test
    public void getAllClientesReturnsList() {
        Customer first = new Customer("11111111111", "Joao Silva", "joao.silva@email.com");
        first.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        Customer second = new Customer("22222222222", "Maria Souza", "maria.souza@email.com");
        second.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        when(customerRepository.findAllAsList()).thenReturn(List.of(first, second));

        RestAssured.given()
                .get("/clientes")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(first.getId().toString()))
                .body("[0].name", equalTo("Joao Silva"))
                .body("[1].id", equalTo(second.getId().toString()))
                .body("[1].name", equalTo("Maria Souza"));
    }

    @Test
    public void criarClienteReturnsCreatedCustomer() {
        Customer input = new Customer("12345678900", "Joao Silva", "joao.silva@email.com");
        input.setPhoneNumber("11999999999");

        Customer saved = new Customer("12345678900", "Joao Silva", "joao.silva@email.com");
        saved.setId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        saved.setPhoneNumber("11999999999");

        when(customerMapper.toDomain(any(CustomerDTO.class))).thenReturn(input);
        when(criarClienteUseCase.executar(input)).thenReturn(saved);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"name\":\"Joao Silva\",\"cpf\":\"12345678900\",\"email\":\"joao.silva@email.com\",\"foneNumber\":\"11999999999\"}")
                .post("/clientes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Joao Silva"))
                .body("cpf", equalTo("12345678900"))
                .body("email", equalTo("joao.silva@email.com"))
                .body("phoneNumber", equalTo("11999999999"));
    }
}
