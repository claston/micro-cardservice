package com.sistema.payments.infra.mapper;

import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.model.PaymentStatus;
import com.sistema.payments.domain.model.PaymentType;
import com.sistema.payments.infra.entity.PaymentEntity;

public final class PaymentEntityMapper {
    private PaymentEntityMapper() {
    }

    public static PaymentEntity toEntity(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId());
        entity.setTenantId(payment.getTenantId());
        entity.setType(payment.getType().name());
        entity.setStatus(payment.getStatus().name());
        entity.setAmountMinor(payment.getAmountMinor());
        entity.setCurrency(payment.getCurrency());
        entity.setReferenceType(payment.getReferenceType());
        entity.setReferenceId(payment.getReferenceId());
        entity.setIdempotencyKey(payment.getIdempotencyKey());
        entity.setExternalProvider(payment.getExternalProvider());
        entity.setExternalPaymentId(payment.getExternalPaymentId());
        entity.setExternalTxid(payment.getExternalTxid());
        entity.setQrCode(payment.getQrCode());
        entity.setCopyPaste(payment.getCopyPaste());
        entity.setExpiresAt(payment.getExpiresAt());
        entity.setCreatedAt(payment.getCreatedAt());
        entity.setUpdatedAt(payment.getUpdatedAt());
        entity.setConfirmedAt(payment.getConfirmedAt());
        entity.setFailureReason(payment.getFailureReason());
        entity.setWalletFromAccountId(payment.getWalletFromAccountId());
        entity.setWalletToAccountId(payment.getWalletToAccountId());
        entity.setLedgerTransactionId(payment.getLedgerTransactionId());
        return entity;
    }

    public static Payment toDomain(PaymentEntity entity) {
        return new Payment(
                entity.getId(),
                entity.getTenantId(),
                PaymentType.valueOf(entity.getType()),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getAmountMinor(),
                entity.getCurrency(),
                entity.getReferenceType(),
                entity.getReferenceId(),
                entity.getIdempotencyKey(),
                entity.getExternalProvider(),
                entity.getExternalPaymentId(),
                entity.getExternalTxid(),
                entity.getQrCode(),
                entity.getCopyPaste(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getConfirmedAt(),
                entity.getFailureReason(),
                entity.getWalletFromAccountId(),
                entity.getWalletToAccountId(),
                entity.getLedgerTransactionId()
        );
    }
}
