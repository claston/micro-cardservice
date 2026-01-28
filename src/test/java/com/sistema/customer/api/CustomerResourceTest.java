package com.sistema.customer.api;

import com.sistema.infraestrutura.repositorio.DbCleanIT;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@QuarkusTest
class CustomerResourceTest extends DbCleanIT {
    private static final String API_KEY = "key-dev";

    @Test
    void createCustomerReturnsCreated() {
        RestAssured.given()
                .header("X-API-Key", API_KEY)
                .contentType("application/json")
                .body("""
                        {
                          "type": "INDIVIDUAL",
                          "name": "Maria da Silva",
                          "documentType": "CPF",
                          "documentNumber": "123.456.789-01"
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("type", equalTo("INDIVIDUAL"))
                .body("name", equalTo("Maria da Silva"))
                .body("documentType", equalTo("CPF"))
                .body("documentNumber", equalTo("12345678901"))
                .body("status", equalTo("ACTIVE"))
                .body("createdAt", notNullValue());
    }

    @Test
    void createCustomerReturns409OnDuplicateDocument() {
        RestAssured.given()
                .header("X-API-Key", API_KEY)
                .contentType("application/json")
                .body("""
                        {
                          "type": "INDIVIDUAL",
                          "name": "Maria da Silva",
                          "documentType": "CPF",
                          "documentNumber": "12345678901"
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(201);

        RestAssured.given()
                .header("X-API-Key", API_KEY)
                .header("X-Request-Id", "req-dup-1")
                .contentType("application/json")
                .body("""
                        {
                          "type": "INDIVIDUAL",
                          "name": "Outra Maria",
                          "documentType": "CPF",
                          "documentNumber": "123.456.789-01"
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(409)
                .header("X-Request-Id", equalTo("req-dup-1"))
                .body("errorCode", equalTo("CUSTOMER_ALREADY_EXISTS"))
                .body("traceId", equalTo("req-dup-1"))
                .body("status", equalTo(409));
    }

    @Test
    void createCustomerReturns401WhenApiKeyMissing() {
        RestAssured.given()
                .header("X-Request-Id", "req-auth-1")
                .contentType("application/json")
                .body("""
                        {
                          "type": "INDIVIDUAL",
                          "name": "Maria da Silva",
                          "documentType": "CPF",
                          "documentNumber": "12345678901"
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(401)
                .header("X-Request-Id", equalTo("req-auth-1"))
                .body("errorCode", equalTo("CUSTOMER_UNAUTHORIZED"))
                .body("traceId", equalTo("req-auth-1"))
                .body("status", equalTo(401));
    }

    @Test
    void createCustomerReturns400WithViolations() {
        RestAssured.given()
                .header("X-API-Key", API_KEY)
                .header("X-Request-Id", "req-val-1")
                .contentType("application/json")
                .body("""
                        {
                          "type": "INVALID",
                          "name": "",
                          "documentType": "CPF",
                          "documentNumber": ""
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("CUSTOMER_VALIDATION_ERROR"))
                .body("traceId", equalTo("req-val-1"))
                .body("violations", not(empty()))
                .body("violations.field", hasItems("type", "name", "documentNumber"));
    }

    @Test
    void getCustomerReturns404WhenNotFound() {
        RestAssured.given()
                .header("X-API-Key", API_KEY)
                .when()
                .get("/customers/11111111-1111-1111-1111-111111111111")
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("CUSTOMER_NOT_FOUND"))
                .body("status", equalTo(404));
    }

    @Test
    void searchByDocumentReturns200WithEmptyItemsWhenNotFound() {
        RestAssured.given()
                .header("X-API-Key", API_KEY)
                .when()
                .get("/customers?documentType=CPF&documentNumber=123.456.789-01&page=0&size=20")
                .then()
                .statusCode(200)
                .body("items", empty())
                .body("total", equalTo(0));
    }

    @Test
    void searchByDocumentReturnsCustomerWhenFound() {
        String id = RestAssured.given()
                .header("X-API-Key", API_KEY)
                .contentType("application/json")
                .body("""
                        {
                          "type": "INDIVIDUAL",
                          "name": "Maria da Silva",
                          "documentType": "CPF",
                          "documentNumber": "12345678901"
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        RestAssured.given()
                .header("X-API-Key", API_KEY)
                .when()
                .get("/customers?documentType=CPF&documentNumber=123.456.789-01&page=0&size=20")
                .then()
                .statusCode(200)
                .body("items.size()", equalTo(1))
                .body("items[0].id", equalTo(id))
                .body("total", equalTo(1));
    }
}

