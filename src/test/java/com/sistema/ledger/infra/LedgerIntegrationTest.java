package com.sistema.ledger.infra;

import com.sistema.infraestrutura.repositorio.DbCleanIT;
import com.sistema.ledger.application.CreateAccountUseCase;
import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.ledger.application.GetAccountStatementUseCase;
import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.application.model.StatementPage;
import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.LedgerTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class LedgerIntegrationTest extends DbCleanIT {

    @Inject
    CreateAccountUseCase createAccountUseCase;

    @Inject
    PostLedgerTransactionUseCase postLedgerTransactionUseCase;

    @Inject
    GetAccountBalanceUseCase getAccountBalanceUseCase;

    @Inject
    GetAccountStatementUseCase getAccountStatementUseCase;

    @Tag("integration-test")
    @Test
    void shouldCreateAccountsPostTransactionAndReturnBalance() {
        java.util.UUID tenantId = java.util.UUID.randomUUID();
        Account debitAccount = createAccountUseCase.execute(
                new CreateAccountCommand(tenantId, "Carteira Cliente", AccountType.ASSET, "BRL", false)
        );
        Account creditAccount = createAccountUseCase.execute(
                new CreateAccountCommand(tenantId, "Receita Loja", AccountType.REVENUE, "BRL", true)
        );
        Account fundingAccount = createAccountUseCase.execute(
                new CreateAccountCommand(tenantId, "Conta Funding", AccountType.ASSET, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
                tenantId,
                "txn-it-1-fund",
                "ext-it-1-fund",
                "Carga inicial",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(fundingAccount.getId(), EntryDirection.DEBIT, 10000, "BRL"),
                        new PostingEntryCommand(debitAccount.getId(), EntryDirection.CREDIT, 10000, "BRL")
                )
        ));

        PostLedgerTransactionCommand command = new PostLedgerTransactionCommand(
                tenantId,
                "txn-it-1",
                "ext-it-1",
                "Compra teste",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(debitAccount.getId(), EntryDirection.DEBIT, 10000, "BRL"),
                        new PostingEntryCommand(creditAccount.getId(), EntryDirection.CREDIT, 10000, "BRL")
                )
        );

        LedgerTransaction transaction = postLedgerTransactionUseCase.execute(command);

        assertNotNull(transaction.getId());
        assertEquals(2, transaction.getEntries().size());

        var debitBalance = getAccountBalanceUseCase.execute(tenantId, debitAccount.getId());
        var creditBalance = getAccountBalanceUseCase.execute(tenantId, creditAccount.getId());

        assertEquals(0L, debitBalance.getBalanceMinor());
        assertEquals(10000L, creditBalance.getBalanceMinor());
    }

    @Tag("integration-test")
    @Test
    void shouldReturnStatementForAccount() {
        java.util.UUID tenantId = java.util.UUID.randomUUID();
        Account debitAccount = createAccountUseCase.execute(
                new CreateAccountCommand(tenantId, "Conta Extrato", AccountType.ASSET, "BRL", true)
        );
        Account creditAccount = createAccountUseCase.execute(
                new CreateAccountCommand(tenantId, "ContraPartida", AccountType.REVENUE, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
                tenantId,
                "txn-it-2",
                "ext-it-2",
                "Compra A",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(debitAccount.getId(), EntryDirection.DEBIT, 5000, "BRL"),
                        new PostingEntryCommand(creditAccount.getId(), EntryDirection.CREDIT, 5000, "BRL")
                )
        ));

        StatementPage statement = getAccountStatementUseCase.execute(
                tenantId,
                debitAccount.getId(),
                null,
                null,
                0,
                10
        );

        assertEquals(debitAccount.getId(), statement.getAccountId());
        assertEquals(1, statement.getItems().size());
        assertEquals(1L, statement.getTotal());
        assertEquals(0, statement.getPage());
    }

    @Tag("integration-test")
    @Test
    void shouldAllowSameIdempotencyKeyAcrossTenants() {
        java.util.UUID tenantA = java.util.UUID.randomUUID();
        java.util.UUID tenantB = java.util.UUID.randomUUID();

        Account debitA = createAccountUseCase.execute(
                new CreateAccountCommand(tenantA, "Conta A", AccountType.ASSET, "BRL", true)
        );
        Account creditA = createAccountUseCase.execute(
                new CreateAccountCommand(tenantA, "Receita A", AccountType.REVENUE, "BRL", true)
        );
        Account debitB = createAccountUseCase.execute(
                new CreateAccountCommand(tenantB, "Conta B", AccountType.ASSET, "BRL", true)
        );
        Account creditB = createAccountUseCase.execute(
                new CreateAccountCommand(tenantB, "Receita B", AccountType.REVENUE, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
                tenantA,
                "txn-it-3",
                "ext-it-3-a",
                "Compra A",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(debitA.getId(), EntryDirection.DEBIT, 1000, "BRL"),
                        new PostingEntryCommand(creditA.getId(), EntryDirection.CREDIT, 1000, "BRL")
                )
        ));

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
                tenantB,
                "txn-it-3",
                "ext-it-3-b",
                "Compra B",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(debitB.getId(), EntryDirection.DEBIT, 1000, "BRL"),
                        new PostingEntryCommand(creditB.getId(), EntryDirection.CREDIT, 1000, "BRL")
                )
        ));

        var balanceA = getAccountBalanceUseCase.execute(tenantA, creditA.getId());
        var balanceB = getAccountBalanceUseCase.execute(tenantB, creditB.getId());

        assertEquals(1000L, balanceA.getBalanceMinor());
        assertEquals(1000L, balanceB.getBalanceMinor());
    }
}
