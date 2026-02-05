package com.sistema.payments.application;

import com.sistema.payments.application.exception.PaymentsInvalidStateException;
import com.sistema.payments.application.exception.PaymentsNotFoundException;
import com.sistema.payments.application.model.PspWebhookEvent;
import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.model.PaymentStatus;
import com.sistema.payments.domain.model.PaymentType;
import com.sistema.payments.domain.repository.PaymentRepository;
import com.sistema.payments.domain.repository.PaymentWebhookEventRepository;
import com.sistema.wallet.application.TransferBetweenWalletAccountsUseCase;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class ProcessPspWebhookUseCase {
    private final PaymentRepository paymentRepository;
    private final PaymentWebhookEventRepository webhookEventRepository;
    private final PaymentsWalletAccountsService walletAccountsService;
    private final TransferBetweenWalletAccountsUseCase transferUseCase;

    public ProcessPspWebhookUseCase(PaymentRepository paymentRepository,
                                    PaymentWebhookEventRepository webhookEventRepository,
                                    PaymentsWalletAccountsService walletAccountsService,
                                    TransferBetweenWalletAccountsUseCase transferUseCase) {
        this.paymentRepository = paymentRepository;
        this.webhookEventRepository = webhookEventRepository;
        this.walletAccountsService = walletAccountsService;
        this.transferUseCase = transferUseCase;
    }

    @Transactional
    public void execute(PspWebhookEvent event) {
        Objects.requireNonNull(event, "event");
        if (event.getExternalPaymentId() == null || event.getExternalPaymentId().isBlank()) {
            throw new IllegalArgumentException("externalPaymentId is required");
        }
        if (event.getStatus() == null || event.getStatus().isBlank()) {
            throw new IllegalArgumentException("status is required");
        }
        String eventType = event.getEventType() == null ? event.getStatus() : event.getEventType();
        String normalizedStatus = event.getStatus().toUpperCase();

        Payment payment = paymentRepository.findByExternalPaymentId(event.getExternalPaymentId())
                .orElseThrow(() -> new PaymentsNotFoundException("payment not found"));

        UUID tenantId = payment.getTenantId();
        boolean registered = webhookEventRepository.registerEvent(tenantId, event.getExternalPaymentId(), eventType, Instant.now());
        if (!registered) {
            return;
        }

        PaymentStatus desiredStatus = PaymentStatus.valueOf(normalizedStatus);
        if (payment.getStatus() == PaymentStatus.CONFIRMED && desiredStatus == PaymentStatus.CONFIRMED) {
            return;
        }

        if (payment.getType() == PaymentType.PIX_CASHIN) {
            handleCashIn(payment, desiredStatus);
        } else if (payment.getType() == PaymentType.PIX_PAYOUT) {
            handlePayout(payment, desiredStatus);
        }
    }

    private void handleCashIn(Payment payment, PaymentStatus desiredStatus) {
        if (desiredStatus == PaymentStatus.CONFIRMED) {
            if (payment.getStatus() == PaymentStatus.CONFIRMED) {
                return;
            }
            payment.setStatus(PaymentStatus.CONFIRMED);
            payment.setConfirmedAt(Instant.now());
            payment.setUpdatedAt(Instant.now());
            paymentRepository.save(payment);

            var cashAccount = walletAccountsService.getOrCreateCashAtPsp(payment.getTenantId(), payment.getCurrency());
            transferUseCase.execute(payment.getTenantId(), new TransferBetweenWalletAccountsCommand(
                    "pay_" + payment.getId() + "_confirm",
                    cashAccount.getId(),
                    payment.getWalletToAccountId(),
                    payment.getAmountMinor(),
                    payment.getCurrency(),
                    "Pix cash-in confirmed"
            ));
            return;
        }

        if (payment.getStatus() == PaymentStatus.CONFIRMED) {
            throw new PaymentsInvalidStateException("payment already confirmed");
        }
        payment.setStatus(desiredStatus);
        payment.setFailureReason(desiredStatus.name());
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);
    }

    private void handlePayout(Payment payment, PaymentStatus desiredStatus) {
        if (desiredStatus == PaymentStatus.CONFIRMED) {
            if (payment.getStatus() == PaymentStatus.CONFIRMED) {
                return;
            }
            payment.setStatus(PaymentStatus.CONFIRMED);
            payment.setConfirmedAt(Instant.now());
            payment.setUpdatedAt(Instant.now());
            paymentRepository.save(payment);

            var cashAccount = walletAccountsService.getOrCreateCashAtPsp(payment.getTenantId(), payment.getCurrency());
            transferUseCase.execute(payment.getTenantId(), new TransferBetweenWalletAccountsCommand(
                    "pay_" + payment.getId() + "_settle",
                    payment.getWalletToAccountId(),
                    cashAccount.getId(),
                    payment.getAmountMinor(),
                    payment.getCurrency(),
                    "Pix payout confirmed"
            ));
            return;
        }

        if (payment.getStatus() == PaymentStatus.CONFIRMED) {
            throw new PaymentsInvalidStateException("payment already confirmed");
        }
        payment.setStatus(desiredStatus);
        payment.setFailureReason(desiredStatus.name());
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        transferUseCase.execute(payment.getTenantId(), new TransferBetweenWalletAccountsCommand(
                "pay_" + payment.getId() + "_rollback",
                payment.getWalletToAccountId(),
                payment.getWalletFromAccountId(),
                payment.getAmountMinor(),
                payment.getCurrency(),
                "Pix payout failed"
        ));
    }
}
