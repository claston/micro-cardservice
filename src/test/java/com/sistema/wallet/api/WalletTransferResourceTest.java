package com.sistema.wallet.api;

import com.sistema.wallet.application.TransferBetweenWalletAccountsUseCase;
import com.sistema.wallet.application.model.WalletTransferResult;
import com.sistema.wallet.application.tenant.TenantResolver;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
public class WalletTransferResourceTest {

    @InjectMock
    TenantResolver tenantResolver;

    @InjectMock
    TransferBetweenWalletAccountsUseCase transferBetweenWalletAccountsUseCase;

    @Test
    public void shouldTransferBetweenAccounts() {
        UUID tenantId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);
        when(transferBetweenWalletAccountsUseCase.execute(eq(tenantId), any()))
                .thenReturn(new WalletTransferResult(transactionId, "POSTED"));

        RestAssured.given()
                .contentType("application/json")
                .header("X-API-Key", "api-key")
                .body("""
                        {
                          "idempotencyKey":"txn-1",
                          "fromAccountId":"%s",
                          "toAccountId":"%s",
                          "amountMinor":500,
                          "currency":"BRL",
                          "description":"transfer"
                        }
                        """.formatted(UUID.randomUUID(), UUID.randomUUID()))
                .post("/transfers")
                .then()
                .statusCode(201)
                .body("transactionId", equalTo(transactionId.toString()))
                .body("status", equalTo("POSTED"));
    }

    @Test
    public void shouldReturnNotFoundWhenAccountMissing() {
        UUID tenantId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);
        when(transferBetweenWalletAccountsUseCase.execute(eq(tenantId), any()))
                .thenThrow(new IllegalArgumentException("wallet account not found: 123"));

        RestAssured.given()
                .contentType("application/json")
                .header("X-API-Key", "api-key")
                .body("""
                        {
                          "idempotencyKey":"txn-2",
                          "fromAccountId":"%s",
                          "toAccountId":"%s",
                          "amountMinor":500,
                          "currency":"BRL"
                        }
                        """.formatted(UUID.randomUUID(), UUID.randomUUID()))
                .post("/transfers")
                .then()
                .statusCode(404);
    }
}
