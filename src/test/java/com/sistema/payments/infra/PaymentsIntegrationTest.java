package com.sistema.payments.infra;

import com.sistema.infraestrutura.repositorio.DbCleanIT;
import com.sistema.ledger.application.CreateAccountUseCase;
import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.payments.application.CreatePixChargeUseCase;
import com.sistema.payments.application.CreatePixPayoutUseCase;
import com.sistema.payments.application.ProcessPspWebhookUseCase;
import com.sistema.payments.application.command.CreatePixChargeCommand;
import com.sistema.payments.application.command.CreatePixPayoutCommand;
import com.sistema.payments.application.model.PixChargeResult;
import com.sistema.payments.application.model.PixPayoutResult;
import com.sistema.payments.application.model.PspWebhookEvent;
import com.sistema.wallet.application.CreateWalletAccountUseCase;
import com.sistema.wallet.application.GetWalletBalanceUseCase;
import com.sistema.wallet.application.command.CreateWalletAccountCommand;
import com.sistema.wallet.domain.model.WalletOwnerType;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class PaymentsIntegrationTest extends DbCleanIT {
    private static final UUID DEFAULT_TENANT = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @jakarta.inject.Inject
    CreateWalletAccountUseCase createWalletAccountUseCase;

    @jakarta.inject.Inject
    GetWalletBalanceUseCase getWalletBalanceUseCase;

    @jakarta.inject.Inject
    CreatePixChargeUseCase createPixChargeUseCase;

    @jakarta.inject.Inject
    CreatePixPayoutUseCase createPixPayoutUseCase;

    @jakarta.inject.Inject
    ProcessPspWebhookUseCase processPspWebhookUseCase;

    @jakarta.inject.Inject
    CreateAccountUseCase createAccountUseCase;

    @jakarta.inject.Inject
    PostLedgerTransactionUseCase postLedgerTransactionUseCase;

    @Tag("integration-test")
    @Test
    void shouldCreateChargeAndConfirmCashIn() {
        var wallet = createWalletAccountUseCase.execute(
                DEFAULT_TENANT,
                new CreateWalletAccountCommand(WalletOwnerType.CUSTOMER, "user-1", "BRL", "Wallet")
        );

        PixChargeResult result = createPixChargeUseCase.execute(
                DEFAULT_TENANT,
                new CreatePixChargeCommand(
                        "ORDER",
                        "order-1",
                        1200,
                        "BRL",
                        "Joao",
                        "123",
                        wallet.getId().toString(),
                        "idemp-charge-1"
                )
        );

        PspWebhookEvent event = new PspWebhookEvent();
        event.setExternalPaymentId(result.getExternalPaymentId());
        event.setStatus("CONFIRMED");
        event.setEventType("CONFIRMED");

        processPspWebhookUseCase.execute(event);

        var balance = getWalletBalanceUseCase.execute(DEFAULT_TENANT, wallet.getId());
        assertEquals(1200L, balance.getBalanceMinor());
    }

    @Tag("integration-test")
    @Test
    void shouldRollbackPayoutOnFailed() {
        var wallet = createWalletAccountUseCase.execute(
                DEFAULT_TENANT,
                new CreateWalletAccountCommand(WalletOwnerType.CUSTOMER, "user-2", "BRL", "Wallet")
        );

        fundWallet(wallet.getLedgerAccountId(), 10000);

        PixPayoutResult payout = createPixPayoutUseCase.execute(
                DEFAULT_TENANT,
                new CreatePixPayoutCommand(
                        "SETTLEMENT",
                        "settlement-1",
                        4000,
                        "BRL",
                        "pix-key",
                        wallet.getId().toString(),
                        "Payout",
                        "idemp-payout-1"
                )
        );

        var balanceAfterReserve = getWalletBalanceUseCase.execute(DEFAULT_TENANT, wallet.getId());
        assertEquals(6000L, balanceAfterReserve.getBalanceMinor());

        PspWebhookEvent event = new PspWebhookEvent();
        event.setExternalPaymentId(payout.getExternalPaymentId());
        event.setStatus("FAILED");
        event.setEventType("FAILED");

        processPspWebhookUseCase.execute(event);

        var balanceAfterRollback = getWalletBalanceUseCase.execute(DEFAULT_TENANT, wallet.getId());
        assertEquals(10000L, balanceAfterRollback.getBalanceMinor());
    }

    private void fundWallet(UUID ledgerAccountId, long amountMinor) {
        var funding = createAccountUseCase.execute(
                new CreateAccountCommand(DEFAULT_TENANT, "Funding", AccountType.ASSET, "BRL", true)
        );

        postLedgerTransactionUseCase.execute(new PostLedgerTransactionCommand(
                DEFAULT_TENANT,
                "fund-" + ledgerAccountId,
                "fund-" + ledgerAccountId,
                "Funding wallet",
                Instant.now(),
                java.util.List.of(
                        new PostingEntryCommand(funding.getId(), EntryDirection.DEBIT, amountMinor, "BRL"),
                        new PostingEntryCommand(ledgerAccountId, EntryDirection.CREDIT, amountMinor, "BRL")
                )
        ));
    }
}
