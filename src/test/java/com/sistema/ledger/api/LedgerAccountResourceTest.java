package com.sistema.ledger.api;

import com.sistema.ledger.application.CreateAccountUseCase;
import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.ledger.application.GetAccountStatementUseCase;
import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.ledger.application.model.StatementItem;
import com.sistema.ledger.application.model.StatementPage;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.LedgerAccount;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
public class LedgerAccountResourceTest {

    @InjectMock
    CreateAccountUseCase createAccountUseCase;

    @InjectMock
    GetAccountBalanceUseCase getAccountBalanceUseCase;

    @InjectMock
    GetAccountStatementUseCase getAccountStatementUseCase;

    @Test
    public void shouldCreateAccount() {
        UUID tenantId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LedgerAccount account = new LedgerAccount(
                accountId,
                tenantId,
                "Cash",
                AccountType.ASSET,
                "BRL",
                false,
                AccountStatus.ACTIVE,
                Instant.now()
        );
        when(createAccountUseCase.execute(any())).thenReturn(account);

        RestAssured.given()
                .contentType("application/json")
                .header("X-Tenant-Id", tenantId.toString())
                .body("{\"name\":\"Cash\",\"type\":\"ASSET\",\"currency\":\"BRL\",\"allowNegative\":false}")
                .post("/ledger/accounts")
                .then()
                .statusCode(201)
                .body("accountId", equalTo(accountId.toString()));
    }

    @Test
    public void shouldReturnBadRequestWhenTenantMissing() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"name\":\"Cash\",\"type\":\"ASSET\",\"currency\":\"BRL\",\"allowNegative\":false}")
                .post("/ledger/accounts")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldGetBalance() {
        UUID tenantId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        when(getAccountBalanceUseCase.execute(eq(tenantId), eq(accountId)))
                .thenReturn(new AccountBalance(accountId, 1250L, "BRL"));

        RestAssured.given()
                .header("X-Tenant-Id", tenantId.toString())
                .get("/ledger/accounts/{id}/balance", accountId)
                .then()
                .statusCode(200)
                .body("accountId", equalTo(accountId.toString()))
                .body("balanceMinor", equalTo(1250))
                .body("currency", equalTo("BRL"));
    }

    @Test
    public void shouldGetStatement() {
        UUID tenantId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        StatementItem item = new StatementItem(
                Instant.parse("2026-01-01T10:00:00Z"),
                UUID.randomUUID(),
                "Initial",
                EntryDirection.CREDIT,
                5000L,
                "BRL"
        );
        StatementPage page = new StatementPage(accountId, List.of(item), 0, 20, 1);
        when(getAccountStatementUseCase.execute(eq(tenantId), eq(accountId), any(), any(), eq(0), eq(20)))
                .thenReturn(page);

        RestAssured.given()
                .header("X-Tenant-Id", tenantId.toString())
                .get("/ledger/accounts/{id}/statement", accountId)
                .then()
                .statusCode(200)
                .body("accountId", equalTo(accountId.toString()))
                .body("page", equalTo(0))
                .body("size", equalTo(20))
                .body("total", equalTo(1))
                .body("items[0].direction", equalTo("CREDIT"))
                .body("items[0].amountMinor", equalTo(5000));
    }
}
