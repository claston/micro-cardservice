package com.sistema.payments.application;

import com.sistema.payments.application.command.CreatePixChargeCommand;
import com.sistema.payments.application.model.PixChargeResult;
import com.sistema.payments.application.provider.ChargeCreated;
import com.sistema.payments.application.provider.PixProviderClient;
import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.model.PaymentStatus;
import com.sistema.payments.domain.model.PaymentType;
import com.sistema.payments.domain.repository.PaymentRepository;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreatePixChargeUseCaseTest {

    @Test
    void shouldCreateChargeAndPersistExternalData() {
        PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        PaymentsWalletAccountsService walletAccountsService = Mockito.mock(PaymentsWalletAccountsService.class);
        PixProviderClient pixProviderClient = Mockito.mock(PixProviderClient.class);

        UUID tenantId = UUID.randomUUID();
        UUID creditWalletId = UUID.randomUUID();
        UUID cashWalletId = UUID.randomUUID();
        WalletAccount creditWallet = walletAccount(creditWalletId, tenantId, "BRL", WalletOwnerType.CUSTOMER);
        WalletAccount cashWallet = walletAccount(cashWalletId, tenantId, "BRL", WalletOwnerType.FUNDING);

        when(paymentRepository.findByIdempotencyKey(tenantId, "idemp-1")).thenReturn(Optional.empty());
        when(walletAccountRepository.findById(tenantId, creditWalletId)).thenReturn(Optional.of(creditWallet));
        when(walletAccountsService.getOrCreateCashAtPsp(tenantId, "BRL")).thenReturn(cashWallet);
        when(pixProviderClient.createCharge(any())).thenReturn(
                new ChargeCreated("ext-1", "txid-1", "qr", "copy", Instant.now().plusSeconds(60))
        );
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreatePixChargeUseCase useCase = new CreatePixChargeUseCase(
                paymentRepository,
                walletAccountRepository,
                walletAccountsService,
                pixProviderClient,
                "FAKE"
        );

        PixChargeResult result = useCase.execute(tenantId, new CreatePixChargeCommand(
                "ORDER",
                "ref-1",
                1200,
                "BRL",
                "Joao",
                "123",
                creditWalletId.toString(),
                "idemp-1"
        ));

        assertEquals("PENDING", result.getStatus());
        assertEquals("ext-1", result.getExternalPaymentId());
        assertEquals("txid-1", result.getTxid());
        assertNotNull(result.getQrCode());
        assertNotNull(result.getCopyPaste());
        assertNotNull(result.getExpiresAt());
        verify(pixProviderClient).createCharge(any());
    }

    @Test
    void shouldReturnExistingOnIdempotency() {
        PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        PaymentsWalletAccountsService walletAccountsService = Mockito.mock(PaymentsWalletAccountsService.class);
        PixProviderClient pixProviderClient = Mockito.mock(PixProviderClient.class);

        UUID tenantId = UUID.randomUUID();
        Payment existing = new Payment(
                UUID.randomUUID(),
                tenantId,
                PaymentType.PIX_CASHIN,
                PaymentStatus.PENDING,
                1200,
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
                UUID.randomUUID(),
                UUID.randomUUID(),
                null
        );
        when(paymentRepository.findByIdempotencyKey(tenantId, "idemp-1")).thenReturn(Optional.of(existing));

        CreatePixChargeUseCase useCase = new CreatePixChargeUseCase(
                paymentRepository,
                walletAccountRepository,
                walletAccountsService,
                pixProviderClient,
                "FAKE"
        );

        PixChargeResult result = useCase.execute(tenantId, new CreatePixChargeCommand(
                "ORDER",
                "ref-1",
                1200,
                "BRL",
                "Joao",
                "123",
                UUID.randomUUID().toString(),
                "idemp-1"
        ));

        assertEquals(existing.getId(), result.getPaymentId());
        assertEquals(existing.getExternalPaymentId(), result.getExternalPaymentId());
        assertEquals(existing.getExternalTxid(), result.getTxid());
        assertEquals(existing.getQrCode(), result.getQrCode());
        assertEquals(existing.getCopyPaste(), result.getCopyPaste());
        assertEquals(existing.getExpiresAt(), result.getExpiresAt());
        verify(pixProviderClient, Mockito.never()).createCharge(any());
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
