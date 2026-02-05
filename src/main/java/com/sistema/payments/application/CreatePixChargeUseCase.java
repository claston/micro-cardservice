package com.sistema.payments.application;

import com.sistema.payments.application.command.CreatePixChargeCommand;
import com.sistema.payments.application.exception.PaymentsNotFoundException;
import com.sistema.payments.application.exception.PaymentsProviderException;
import com.sistema.payments.application.model.PixChargeResult;
import com.sistema.payments.application.provider.ChargeCreated;
import com.sistema.payments.application.provider.CreateChargeParams;
import com.sistema.payments.application.provider.PixProviderClient;
import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.model.PaymentStatus;
import com.sistema.payments.domain.model.PaymentType;
import com.sistema.payments.domain.repository.PaymentRepository;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CreatePixChargeUseCase {
    private final PaymentRepository paymentRepository;
    private final WalletAccountRepository walletAccountRepository;
    private final PaymentsWalletAccountsService walletAccountsService;
    private final PixProviderClient pixProviderClient;
    private final String providerName;

    public CreatePixChargeUseCase(PaymentRepository paymentRepository,
                                  WalletAccountRepository walletAccountRepository,
                                  PaymentsWalletAccountsService walletAccountsService,
                                  PixProviderClient pixProviderClient,
                                  @ConfigProperty(name = "payments.provider", defaultValue = "FAKE") String providerName) {
        this.paymentRepository = paymentRepository;
        this.walletAccountRepository = walletAccountRepository;
        this.walletAccountsService = walletAccountsService;
        this.pixProviderClient = pixProviderClient;
        this.providerName = providerName;
    }

    @Transactional
    public PixChargeResult execute(UUID tenantId, CreatePixChargeCommand command) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(command, "command");

        Payment existing = paymentRepository.findByIdempotencyKey(tenantId, command.getIdempotencyKey())
                .orElse(null);
        if (existing != null) {
            return new PixChargeResult(
                    existing.getId(),
                    existing.getStatus().name(),
                    existing.getExternalPaymentId(),
                    existing.getExternalTxid(),
                    existing.getQrCode(),
                    existing.getCopyPaste(),
                    existing.getExpiresAt()
            );
        }

        UUID creditAccountId = UUID.fromString(command.getCreditToWalletAccountId());
        walletAccountRepository.findById(tenantId, creditAccountId)
                .orElseThrow(() -> new PaymentsNotFoundException("wallet account not found"));

        var cashAccount = walletAccountsService.getOrCreateCashAtPsp(tenantId, command.getCurrency());

        Instant now = Instant.now();
        Payment payment = new Payment(
                UUID.randomUUID(),
                tenantId,
                PaymentType.PIX_CASHIN,
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
                cashAccount.getId(),
                creditAccountId,
                null
        );
        paymentRepository.save(payment);

        ChargeCreated created;
        try {
            created = pixProviderClient.createCharge(new CreateChargeParams(
                    command.getReferenceId(),
                    command.getAmountMinor(),
                    command.getCurrency(),
                    command.getPayerName(),
                    command.getPayerDocument()
            ));
        } catch (RuntimeException ex) {
            throw new PaymentsProviderException("failed to create pix charge");
        }

        payment.setExternalPaymentId(created.getExternalPaymentId());
        payment.setExternalTxid(created.getTxid());
        payment.setQrCode(created.getQrCode());
        payment.setCopyPaste(created.getCopyPaste());
        payment.setExpiresAt(created.getExpiresAt());
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        return new PixChargeResult(
                payment.getId(),
                payment.getStatus().name(),
                created.getExternalPaymentId(),
                created.getTxid(),
                created.getQrCode(),
                created.getCopyPaste(),
                created.getExpiresAt()
        );
    }
}
