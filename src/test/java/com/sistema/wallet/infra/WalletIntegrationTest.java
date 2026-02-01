package com.sistema.wallet.infra;

import com.sistema.infraestrutura.repositorio.DbCleanIT;
import com.sistema.ledger.application.CreateAccountUseCase;
import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.wallet.application.CreateWalletAccountUseCase;
import com.sistema.wallet.application.GetWalletBalanceUseCase;
import com.sistema.wallet.application.TransferBetweenWalletAccountsUseCase;
import com.sistema.wallet.application.command.CreateWalletAccountCommand;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.domain.model.WalletOwnerType;
import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
class WalletIntegrationTest extends DbCleanIT {

    private static final UUID DEFAULT_TENANT = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Inject
    AgroalDataSource dataSource;

    @Inject
    CreateWalletAccountUseCase createWalletAccountUseCase;

    @Inject
    GetWalletBalanceUseCase getWalletBalanceUseCase;

    @Inject
    TransferBetweenWalletAccountsUseCase transferBetweenWalletAccountsUseCase;

    @Inject
    CreateAccountUseCase createAccountUseCase;

    @Inject
    PostLedgerTransactionUseCase postLedgerTransactionUseCase;

    @Tag("integration-test")
    @Test
    void shouldCreateWalletAccountsAndTransfer() {
        var walletA = createWalletAccountUseCase.execute(
                DEFAULT_TENANT,
                new CreateWalletAccountCommand(WalletOwnerType.CUSTOMER, "user-a", "BRL", "Wallet A")
        );
        var walletB = createWalletAccountUseCase.execute(
                DEFAULT_TENANT,
                new CreateWalletAccountCommand(WalletOwnerType.CUSTOMER, "user-b", "BRL", "Wallet B")
        );

        var funding = createAccountUseCase.execute(
                new CreateAccountCommand(DEFAULT_TENANT, "Funding", AccountType.ASSET, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
                DEFAULT_TENANT,
                "fund-wallet-a",
                "fund-wallet-a",
                "Funding A",
                Instant.now(),
                java.util.List.of(
                        new PostingEntryCommand(funding.getId(), EntryDirection.DEBIT, 10000, "BRL"),
                        new PostingEntryCommand(walletA.getLedgerAccountId(), EntryDirection.CREDIT, 10000, "BRL")
                )
        ));

        transferBetweenWalletAccountsUseCase.execute(
                DEFAULT_TENANT,
                new TransferBetweenWalletAccountsCommand(
                        "transfer-1",
                        walletA.getId(),
                        walletB.getId(),
                        5000,
                        "BRL",
                        "Wallet transfer"
                )
        );

        var balanceA = getWalletBalanceUseCase.execute(DEFAULT_TENANT, walletA.getId());
        var balanceB = getWalletBalanceUseCase.execute(DEFAULT_TENANT, walletB.getId());

        assertEquals(5000L, balanceA.getBalanceMinor());
        assertEquals(5000L, balanceB.getBalanceMinor());
    }

    @Tag("integration-test")
    @Test
    void shouldAllowFundingOwnerTypeForFundingWalletTransfers() {
        var fundingWallet = createWalletAccountUseCase.execute(
                DEFAULT_TENANT,
                new CreateWalletAccountCommand(WalletOwnerType.FUNDING, "funding", "BRL", "Funding Wallet")
        );
        var customerWallet = createWalletAccountUseCase.execute(
                DEFAULT_TENANT,
                new CreateWalletAccountCommand(WalletOwnerType.CUSTOMER, "user-1", "BRL", "Customer Wallet")
        );

        var fundingAccount = createAccountUseCase.execute(
                new CreateAccountCommand(DEFAULT_TENANT, "Funding", AccountType.ASSET, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
                DEFAULT_TENANT,
                "fund-funding-wallet",
                "fund-funding-wallet",
                "Fund funding wallet",
                Instant.now(),
                java.util.List.of(
                        new PostingEntryCommand(fundingAccount.getId(), EntryDirection.DEBIT, 20000, "BRL"),
                        new PostingEntryCommand(fundingWallet.getLedgerAccountId(), EntryDirection.CREDIT, 20000, "BRL")
                )
        ));

        transferBetweenWalletAccountsUseCase.execute(
                DEFAULT_TENANT,
                new TransferBetweenWalletAccountsCommand(
                        "funding-to-customer-1",
                        fundingWallet.getId(),
                        customerWallet.getId(),
                        7000,
                        "BRL",
                        "Funding via funding wallet"
                )
        );

        var fundingBalance = getWalletBalanceUseCase.execute(DEFAULT_TENANT, fundingWallet.getId());
        var customerBalance = getWalletBalanceUseCase.execute(DEFAULT_TENANT, customerWallet.getId());

        assertEquals(13000L, fundingBalance.getBalanceMinor());
        assertEquals(7000L, customerBalance.getBalanceMinor());
    }

    @Tag("integration-test")
    @Test
    void shouldAllowSameOwnerAcrossTenants() throws Exception {
        UUID tenantB = UUID.randomUUID();
        insertTenant(tenantB, "Tenant B");

        var walletDefault = createWalletAccountUseCase.execute(
                DEFAULT_TENANT,
                new CreateWalletAccountCommand(WalletOwnerType.CUSTOMER, "user-1", "BRL", "Default Wallet")
        );

        var walletOther = createWalletAccountUseCase.execute(
                tenantB,
                new CreateWalletAccountCommand(WalletOwnerType.CUSTOMER, "user-1", "BRL", "Other Wallet")
        );

        assertNotEquals(walletDefault.getId(), walletOther.getId());
    }

    private void insertTenant(UUID tenantId, String name) throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO tenants (id, name, status, created_at) VALUES (?, ?, 'ACTIVE', CURRENT_TIMESTAMP)"
             )) {
            ps.setObject(1, tenantId);
            ps.setString(2, name);
            ps.executeUpdate();
        }
    }
}
