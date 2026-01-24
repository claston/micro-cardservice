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
        Account debitAccount = createAccountUseCase.execute(
                new CreateAccountCommand("Carteira Cliente", AccountType.ASSET, "BRL", false)
        );
        Account creditAccount = createAccountUseCase.execute(
                new CreateAccountCommand("Receita Loja", AccountType.REVENUE, "BRL", true)
        );
        Account fundingAccount = createAccountUseCase.execute(
                new CreateAccountCommand("Conta Funding", AccountType.ASSET, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
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

        var debitBalance = getAccountBalanceUseCase.execute(debitAccount.getId());
        var creditBalance = getAccountBalanceUseCase.execute(creditAccount.getId());

        assertEquals(0L, debitBalance.getBalanceMinor());
        assertEquals(10000L, creditBalance.getBalanceMinor());
    }

    @Tag("integration-test")
    @Test
    void shouldReturnStatementForAccount() {
        Account debitAccount = createAccountUseCase.execute(
                new CreateAccountCommand("Conta Extrato", AccountType.ASSET, "BRL", true)
        );
        Account creditAccount = createAccountUseCase.execute(
                new CreateAccountCommand("ContraPartida", AccountType.REVENUE, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
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
}
