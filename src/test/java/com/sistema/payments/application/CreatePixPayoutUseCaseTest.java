package com.sistema.payments.application;

import com.sistema.payments.application.command.CreatePixPayoutCommand;
import com.sistema.payments.application.exception.PaymentsInsufficientBalanceException;
import com.sistema.payments.application.model.PixPayoutResult;
import com.sistema.payments.application.provider.PayoutCreated;
import com.sistema.payments.application.provider.PixProviderClient;
import com.sistema.payments.domain.repository.PaymentRepository;
import com.sistema.wallet.application.TransferBetweenWalletAccountsUseCase;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.exception.WalletInsufficientBalanceException;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreatePixPayoutUseCaseTest {

    @Test
    void shouldReserveAndCreatePayout() {
        PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        PaymentsWalletAccountsService walletAccountsService = Mockito.mock(PaymentsWalletAccountsService.class);
        TransferBetweenWalletAccountsUseCase transferUseCase = Mockito.mock(TransferBetweenWalletAccountsUseCase.class);
        PixProviderClient pixProviderClient = Mockito.mock(PixProviderClient.class);

        UUID tenantId = UUID.randomUUID();
        UUID debitWalletId = UUID.randomUUID();
        UUID clearingWalletId = UUID.randomUUID();
        WalletAccount debitWallet = walletAccount(debitWalletId, tenantId, "BRL", WalletOwnerType.CUSTOMER);
        WalletAccount clearingWallet = walletAccount(clearingWalletId, tenantId, "BRL", WalletOwnerType.INTERNAL);

        when(paymentRepository.findByIdempotencyKey(tenantId, "idemp-1")).thenReturn(Optional.empty());
        when(walletAccountRepository.findById(tenantId, debitWalletId)).thenReturn(Optional.of(debitWallet));
        when(walletAccountsService.getOrCreateOutboundClearing(tenantId, "BRL")).thenReturn(clearingWallet);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pixProviderClient.createPayout(any())).thenReturn(new PayoutCreated("ext-payout"));

        CreatePixPayoutUseCase useCase = new CreatePixPayoutUseCase(
                paymentRepository,
                walletAccountRepository,
                walletAccountsService,
                transferUseCase,
                pixProviderClient,
                "FAKE"
        );

        PixPayoutResult result = useCase.execute(tenantId, new CreatePixPayoutCommand(
                "SETTLEMENT",
                "ref-1",
                5000,
                "BRL",
                "pix-key",
                debitWalletId.toString(),
                "Payout",
                "idemp-1"
        ));

        assertEquals("PENDING", result.getStatus());
        assertEquals("ext-payout", result.getExternalPaymentId());

        ArgumentCaptor<TransferBetweenWalletAccountsCommand> transferCaptor =
                ArgumentCaptor.forClass(TransferBetweenWalletAccountsCommand.class);
        verify(transferUseCase).execute(eq(tenantId), transferCaptor.capture());
        TransferBetweenWalletAccountsCommand transfer = transferCaptor.getValue();
        assertEquals(debitWalletId, transfer.getFromAccountId());
        assertEquals(clearingWalletId, transfer.getToAccountId());
        assertEquals(5000, transfer.getAmountMinor());
    }

    @Test
    void shouldRejectInsufficientBalance() {
        PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        PaymentsWalletAccountsService walletAccountsService = Mockito.mock(PaymentsWalletAccountsService.class);
        TransferBetweenWalletAccountsUseCase transferUseCase = Mockito.mock(TransferBetweenWalletAccountsUseCase.class);
        PixProviderClient pixProviderClient = Mockito.mock(PixProviderClient.class);

        UUID tenantId = UUID.randomUUID();
        UUID debitWalletId = UUID.randomUUID();
        WalletAccount debitWallet = walletAccount(debitWalletId, tenantId, "BRL", WalletOwnerType.CUSTOMER);
        WalletAccount clearingWallet = walletAccount(UUID.randomUUID(), tenantId, "BRL", WalletOwnerType.INTERNAL);

        when(paymentRepository.findByIdempotencyKey(tenantId, "idemp-2")).thenReturn(Optional.empty());
        when(walletAccountRepository.findById(tenantId, debitWalletId)).thenReturn(Optional.of(debitWallet));
        when(walletAccountsService.getOrCreateOutboundClearing(tenantId, "BRL")).thenReturn(clearingWallet);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferUseCase.execute(eq(tenantId), any()))
                .thenThrow(new WalletInsufficientBalanceException("insufficient balance"));

        CreatePixPayoutUseCase useCase = new CreatePixPayoutUseCase(
                paymentRepository,
                walletAccountRepository,
                walletAccountsService,
                transferUseCase,
                pixProviderClient,
                "FAKE"
        );

        assertThrows(PaymentsInsufficientBalanceException.class, () -> useCase.execute(tenantId, new CreatePixPayoutCommand(
                "SETTLEMENT",
                "ref-2",
                5000,
                "BRL",
                "pix-key",
                debitWalletId.toString(),
                "Payout",
                "idemp-2"
        )));
    }

    private WalletAccount walletAccount(UUID id, UUID tenantId, String currency, WalletOwnerType ownerType) {
        return new WalletAccount(
                id,
                tenantId,
                ownerType,
                "owner",
                currency,
                WalletAccountStatus.ACTIVE,
                null,
                UUID.randomUUID(),
                Instant.now()
        );
    }
}
