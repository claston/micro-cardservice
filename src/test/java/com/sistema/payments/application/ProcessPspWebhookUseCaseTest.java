package com.sistema.payments.application;

import com.sistema.payments.application.model.PspWebhookEvent;
import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.model.PaymentStatus;
import com.sistema.payments.domain.model.PaymentType;
import com.sistema.payments.domain.repository.PaymentRepository;
import com.sistema.payments.domain.repository.PaymentWebhookEventRepository;
import com.sistema.wallet.application.TransferBetweenWalletAccountsUseCase;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.model.WalletTransferResult;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessPspWebhookUseCaseTest {

    @Test
    void shouldConfirmCashInAndTransfer() {
        PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        PaymentWebhookEventRepository webhookEventRepository = Mockito.mock(PaymentWebhookEventRepository.class);
        PaymentsWalletAccountsService walletAccountsService = Mockito.mock(PaymentsWalletAccountsService.class);
        TransferBetweenWalletAccountsUseCase transferUseCase = Mockito.mock(TransferBetweenWalletAccountsUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID cashWalletId = UUID.randomUUID();
        UUID creditWalletId = UUID.randomUUID();
        UUID ledgerTransactionId = UUID.randomUUID();
        Payment payment = new Payment(
                paymentId,
                tenantId,
                PaymentType.PIX_CASHIN,
                PaymentStatus.PENDING,
                1000,
                "BRL",
                "ORDER",
                "ref-1",
                "idemp-1",
                "FAKE",
                "ext-1",
                "txid-1",
                "qr",
                "copy",
                Instant.now().plusSeconds(60),
                Instant.now(),
                Instant.now(),
                null,
                null,
                cashWalletId,
                creditWalletId,
                null
        );
        when(paymentRepository.findByExternalPaymentId("ext-1")).thenReturn(Optional.of(payment));
        when(webhookEventRepository.registerEvent(eq(tenantId), eq("ext-1"), any(), any())).thenReturn(true);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletAccountsService.getOrCreateCashAtPsp(tenantId, "BRL"))
                .thenReturn(walletAccount(cashWalletId, tenantId, WalletOwnerType.FUNDING));
        when(transferUseCase.execute(eq(tenantId), any()))
                .thenReturn(new WalletTransferResult(ledgerTransactionId, "POSTED"));

        ProcessPspWebhookUseCase useCase = new ProcessPspWebhookUseCase(
                paymentRepository,
                webhookEventRepository,
                walletAccountsService,
                transferUseCase
        );

        PspWebhookEvent event = new PspWebhookEvent();
        event.setExternalPaymentId("ext-1");
        event.setStatus("CONFIRMED");
        event.setEventType("CONFIRMED");

        useCase.execute(event);

        ArgumentCaptor<TransferBetweenWalletAccountsCommand> transferCaptor =
                ArgumentCaptor.forClass(TransferBetweenWalletAccountsCommand.class);
        verify(transferUseCase).execute(eq(tenantId), transferCaptor.capture());
        TransferBetweenWalletAccountsCommand command = transferCaptor.getValue();
        assertEquals(cashWalletId, command.getFromAccountId());
        assertEquals(creditWalletId, command.getToAccountId());
        assertEquals("pay_" + paymentId + "_confirm", command.getIdempotencyKey());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, atLeastOnce()).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(ledgerTransactionId, savedPayment.getLedgerTransactionId());
    }

    @Test
    void shouldRollbackPayoutOnFailed() {
        PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        PaymentWebhookEventRepository webhookEventRepository = Mockito.mock(PaymentWebhookEventRepository.class);
        PaymentsWalletAccountsService walletAccountsService = Mockito.mock(PaymentsWalletAccountsService.class);
        TransferBetweenWalletAccountsUseCase transferUseCase = Mockito.mock(TransferBetweenWalletAccountsUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID debitWalletId = UUID.randomUUID();
        UUID clearingWalletId = UUID.randomUUID();
        Payment payment = new Payment(
                paymentId,
                tenantId,
                PaymentType.PIX_PAYOUT,
                PaymentStatus.PENDING,
                1000,
                "BRL",
                "SETTLEMENT",
                "ref-2",
                "idemp-2",
                "FAKE",
                "ext-2",
                "txid-2",
                null,
                null,
                null,
                Instant.now(),
                Instant.now(),
                null,
                null,
                debitWalletId,
                clearingWalletId,
                null
        );
        when(paymentRepository.findByExternalPaymentId("ext-2")).thenReturn(Optional.of(payment));
        when(webhookEventRepository.registerEvent(eq(tenantId), eq("ext-2"), any(), any())).thenReturn(true);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProcessPspWebhookUseCase useCase = new ProcessPspWebhookUseCase(
                paymentRepository,
                webhookEventRepository,
                walletAccountsService,
                transferUseCase
        );

        PspWebhookEvent event = new PspWebhookEvent();
        event.setExternalPaymentId("ext-2");
        event.setStatus("FAILED");
        event.setEventType("FAILED");

        useCase.execute(event);

        ArgumentCaptor<TransferBetweenWalletAccountsCommand> transferCaptor =
                ArgumentCaptor.forClass(TransferBetweenWalletAccountsCommand.class);
        verify(transferUseCase).execute(eq(tenantId), transferCaptor.capture());
        TransferBetweenWalletAccountsCommand command = transferCaptor.getValue();
        assertEquals(clearingWalletId, command.getFromAccountId());
        assertEquals(debitWalletId, command.getToAccountId());
        assertEquals("pay_" + paymentId + "_rollback", command.getIdempotencyKey());
    }

    private WalletAccount walletAccount(UUID id, UUID tenantId, WalletOwnerType ownerType) {
        return new WalletAccount(
                id,
                tenantId,
                ownerType,
                "owner",
                "BRL",
                WalletAccountStatus.ACTIVE,
                null,
                UUID.randomUUID(),
                Instant.now()
        );
    }
}
