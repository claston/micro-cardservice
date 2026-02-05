package com.sistema.payments.application;

import com.sistema.payments.application.command.CreatePixPayoutCommand;
import com.sistema.payments.application.exception.PaymentsInsufficientBalanceException;
import com.sistema.payments.application.exception.PaymentsNotFoundException;
import com.sistema.payments.application.exception.PaymentsProviderException;
import com.sistema.payments.application.model.PixPayoutResult;
import com.sistema.payments.application.provider.CreatePayoutParams;
import com.sistema.payments.application.provider.PayoutCreated;
import com.sistema.payments.application.provider.PixProviderClient;
import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.model.PaymentStatus;
import com.sistema.payments.domain.model.PaymentType;
import com.sistema.payments.domain.repository.PaymentRepository;
import com.sistema.wallet.application.TransferBetweenWalletAccountsUseCase;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.exception.WalletInsufficientBalanceException;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CreatePixPayoutUseCase {
    private final PaymentRepository paymentRepository;
    private final WalletAccountRepository walletAccountRepository;
    private final PaymentsWalletAccountsService walletAccountsService;
    private final TransferBetweenWalletAccountsUseCase transferUseCase;
    private final PixProviderClient pixProviderClient;
    private final String providerName;

    public CreatePixPayoutUseCase(PaymentRepository paymentRepository,
                                  WalletAccountRepository walletAccountRepository,
                                  PaymentsWalletAccountsService walletAccountsService,
                                  TransferBetweenWalletAccountsUseCase transferUseCase,
                                  PixProviderClient pixProviderClient,
                                  @ConfigProperty(name = "payments.provider", defaultValue = "FAKE") String providerName) {
        this.paymentRepository = paymentRepository;
        this.walletAccountRepository = walletAccountRepository;
        this.walletAccountsService = walletAccountsService;
        this.transferUseCase = transferUseCase;
        this.pixProviderClient = pixProviderClient;
        this.providerName = providerName;
    }

    @Transactional
    public PixPayoutResult execute(UUID tenantId, CreatePixPayoutCommand command) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(command, "command");

        Payment existing = paymentRepository.findByIdempotencyKey(tenantId, command.getIdempotencyKey())
                .orElse(null);
        if (existing != null) {
            return new PixPayoutResult(existing.getId(), existing.getStatus().name(), existing.getExternalPaymentId());
        }

        UUID debitAccountId = UUID.fromString(command.getDebitFromWalletAccountId());
        var debitAccount = walletAccountRepository.findById(tenantId, debitAccountId)
                .orElseThrow(() -> new PaymentsNotFoundException("wallet account not found"));
        if (!debitAccount.getCurrency().equals(command.getCurrency())) {
            throw new IllegalArgumentException("currency mismatch");
        }

        var clearingAccount = walletAccountsService.getOrCreateOutboundClearing(tenantId, command.getCurrency());

        Instant now = Instant.now();
        Payment payment = new Payment(
                UUID.randomUUID(),
                tenantId,
                PaymentType.PIX_PAYOUT,
                PaymentStatus.PENDING,
                command.getAmountMinor(),
                command.getCurrency(),
                command.getReferenceType(),
                command.getReferenceId(),
                command.getIdempotencyKey(),
                providerName,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null,
                null,
                debitAccountId,
                clearingAccount.getId(),
                null
        );
        paymentRepository.save(payment);

        try {
            transferUseCase.execute(tenantId, new TransferBetweenWalletAccountsCommand(
                    "pay_" + payment.getId() + "_reserve",
                    debitAccountId,
                    clearingAccount.getId(),
                    command.getAmountMinor(),
                    command.getCurrency(),
                    command.getDescription()
            ));
        } catch (WalletInsufficientBalanceException ex) {
            throw new PaymentsInsufficientBalanceException(ex.getMessage());
        }

        PayoutCreated created;
        try {
            created = pixProviderClient.createPayout(new CreatePayoutParams(
                    command.getReferenceId(),
                    command.getAmountMinor(),
                    command.getCurrency(),
                    command.getPixKey(),
                    command.getDescription()
            ));
        } catch (RuntimeException ex) {
            throw new PaymentsProviderException("failed to create pix payout");
        }

        payment.setExternalPaymentId(created.getExternalPaymentId());
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        return new PixPayoutResult(payment.getId(), payment.getStatus().name(), created.getExternalPaymentId());
    }
}
