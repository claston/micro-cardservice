package com.sistema.payments.domain.repository;

import com.sistema.payments.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(UUID tenantId, UUID paymentId);

    Optional<Payment> findByIdempotencyKey(UUID tenantId, String idempotencyKey);

    Optional<Payment> findByExternalPaymentId(UUID tenantId, String externalPaymentId);

    Optional<Payment> findByExternalPaymentId(String externalPaymentId);

    Optional<Payment> findByReference(UUID tenantId, String referenceType, String referenceId);
}
