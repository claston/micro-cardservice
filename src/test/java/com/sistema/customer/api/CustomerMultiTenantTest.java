package com.sistema.customer.api;

import com.sistema.infraestrutura.repositorio.DbCleanIT;
import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestProfile(CustomerMultiTenantProfile.class)
class CustomerMultiTenantTest extends DbCleanIT {
    static final UUID TENANT_A = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    static final UUID TENANT_B = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Inject
    AgroalDataSource dataSource;

    @BeforeEach
    void ensureTenantsExist() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            insertTenant(conn, TENANT_A, "TENANT_A");
            insertTenant(conn, TENANT_B, "TENANT_B");
        }
    }

    private static void insertTenant(Connection conn, UUID id, String name) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "insert into tenants (id, name, status, created_at) values (?, ?, 'ACTIVE', current_timestamp)")) {
            ps.setObject(1, id);
            ps.setString(2, name);
            try {
                ps.executeUpdate();
            } catch (Exception ignored) {
                // already exists
            }
        }
    }

    @Test
    void sameDocumentCanExistInDifferentTenants() {
        String customerA = RestAssured.given()
                .header("X-API-Key", "key-a")
                .contentType("application/json")
                .body("""
                        {
                          "type": "INDIVIDUAL",
                          "name": "Maria A",
                          "documentType": "CPF",
                          "documentNumber": "12345678901"
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().path("id");

        RestAssured.given()
                .header("X-API-Key", "key-b")
                .contentType("application/json")
                .body("""
                        {
                          "type": "INDIVIDUAL",
                          "name": "Maria B",
                          "documentType": "CPF",
                          "documentNumber": "12345678901"
                        }
                        """)
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue());

        RestAssured.given()
                .header("X-API-Key", "key-b")
                .when()
                .get("/customers/" + customerA)
                .then()
                .statusCode(404)
                .body("errorCode", equalTo("CUSTOMER_NOT_FOUND"));
    }
}

