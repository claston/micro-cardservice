package com.sistema.wallet.api;

import com.sistema.wallet.application.CreateWalletAccountUseCase;
import com.sistema.wallet.application.GetWalletBalanceUseCase;
import com.sistema.wallet.application.GetWalletStatementUseCase;
import com.sistema.wallet.application.exception.WalletAccountAlreadyExistsException;
import com.sistema.wallet.application.exception.WalletUnauthorizedException;
import com.sistema.wallet.application.model.WalletBalance;
import com.sistema.wallet.application.model.WalletStatementItem;
import com.sistema.wallet.application.model.WalletStatementPage;
import com.sistema.wallet.application.tenant.TenantResolver;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
public class WalletAccountResourceTest {

    @InjectMock
    TenantResolver tenantResolver;

    @InjectMock
    CreateWalletAccountUseCase createWalletAccountUseCase;

    @InjectMock
    GetWalletBalanceUseCase getWalletBalanceUseCase;

    @InjectMock
    GetWalletStatementUseCase getWalletStatementUseCase;

    @Test
    public void shouldCreateWalletAccount() {
        UUID tenantId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);
        WalletAccount account = new WalletAccount(
                accountId,
                tenantId,
                WalletOwnerType.CUSTOMER,
                "user-1",
                "BRL",
                WalletAccountStatus.ACTIVE,
                "Main",
                UUID.randomUUID(),
                Instant.now()
        );
        when(createWalletAccountUseCase.execute(eq(tenantId), any())).thenReturn(account);

        RestAssured.given()
                .contentType("application/json")
                .header("X-API-Key", "api-key")
                .body("{\"ownerType\":\"CUSTOMER\",\"ownerId\":\"user-1\",\"currency\":\"BRL\",\"label\":\"Main\"}")
                .post("/accounts")
                .then()
                .statusCode(201)
                .body("accountId", equalTo(accountId.toString()))
                .body("ownerType", equalTo("CUSTOMER"))
                .body("ownerId", equalTo("user-1"))
                .body("currency", equalTo("BRL"))
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    public void shouldReturnConflictWhenAccountExists() {
        UUID tenantId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);
        when(createWalletAccountUseCase.execute(eq(tenantId), any()))
                .thenThrow(new WalletAccountAlreadyExistsException());

        RestAssured.given()
                .contentType("application/json")
                .header("X-API-Key", "api-key")
                .body("{\"ownerType\":\"CUSTOMER\",\"ownerId\":\"user-1\",\"currency\":\"BRL\"}")
                .post("/accounts")
                .then()
                .statusCode(409)
                .body("errorCode", equalTo("WALLET_ACCOUNT_ALREADY_EXISTS"))
                .body("traceId", notNullValue());
    }

    @Test
    public void shouldRejectInvalidApiKey() {
        when(tenantResolver.resolveTenantId("bad")).thenThrow(new WalletUnauthorizedException("apiKey not recognized"));

        RestAssured.given()
                .contentType("application/json")
                .header("X-API-Key", "bad")
                .body("{\"ownerType\":\"CUSTOMER\",\"ownerId\":\"user-1\",\"currency\":\"BRL\"}")
                .post("/accounts")
                .then()
                .statusCode(401)
                .body("errorCode", equalTo("WALLET_UNAUTHORIZED"))
                .body("traceId", notNullValue());
    }

    @Test
    public void shouldReturnValidationWhenOwnerTypeInvalid() {
        UUID tenantId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);

        RestAssured.given()
                .contentType("application/json")
                .header("X-API-Key", "api-key")
                .body("{\"ownerType\":\"FUNDER\",\"ownerId\":\"user-1\",\"currency\":\"BRL\"}")
                .post("/accounts")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("WALLET_VALIDATION_ERROR"))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("ownerType"))
                .body("traceId", notNullValue());
    }

    @Test
    public void shouldReturnValidationWhenOwnerIdMissing() {
        UUID tenantId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);

        RestAssured.given()
                .contentType("application/json")
                .header("X-API-Key", "api-key")
                .body("{\"ownerType\":\"CUSTOMER\",\"currency\":\"BRL\"}")
                .post("/accounts")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("WALLET_VALIDATION_ERROR"))
                .body("violations", hasSize(1))
                .body("violations[0].field", equalTo("ownerId"))
                .body("traceId", notNullValue());
    }

    @Test
    public void shouldRejectMissingApiKey() {
        when(tenantResolver.resolveTenantId(null)).thenThrow(new WalletUnauthorizedException("apiKey is required"));

        RestAssured.given()
                .contentType("application/json")
                .body("{\"ownerType\":\"CUSTOMER\",\"ownerId\":\"user-1\",\"currency\":\"BRL\"}")
                .post("/accounts")
                .then()
                .statusCode(401)
                .body("errorCode", equalTo("WALLET_UNAUTHORIZED"))
                .body("traceId", notNullValue());
    }

    @Test
    public void shouldGetWalletBalance() {
        UUID tenantId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);
        when(getWalletBalanceUseCase.execute(eq(tenantId), eq(accountId)))
                .thenReturn(new WalletBalance(accountId, 2000L, "BRL"));

        RestAssured.given()
                .header("X-API-Key", "api-key")
                .get("/accounts/{id}/balance", accountId)
                .then()
                .statusCode(200)
                .body("accountId", equalTo(accountId.toString()))
                .body("balanceMinor", equalTo(2000))
                .body("currency", equalTo("BRL"));
    }

    @Test
    public void shouldGetWalletStatement() {
        UUID tenantId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        when(tenantResolver.resolveTenantId("api-key")).thenReturn(tenantId);
        WalletStatementItem item = new WalletStatementItem(
                Instant.parse("2026-01-01T10:00:00Z"),
                UUID.randomUUID(),
                "Transfer",
                "DEBIT",
                500L,
                "BRL"
        );
        WalletStatementPage page = new WalletStatementPage(accountId, List.of(item), 0, 20, 1);
        when(getWalletStatementUseCase.execute(eq(tenantId), eq(accountId), any(), any(), eq(0), eq(20)))
                .thenReturn(page);

        RestAssured.given()
                .header("X-API-Key", "api-key")
                .get("/accounts/{id}/statement", accountId)
                .then()
                .statusCode(200)
                .body("accountId", equalTo(accountId.toString()))
                .body("items[0].direction", equalTo("DEBIT"))
                .body("items[0].amountMinor", equalTo(500));
    }
}
