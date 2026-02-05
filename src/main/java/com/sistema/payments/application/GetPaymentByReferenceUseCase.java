package com.sistema.payments.application;

import com.sistema.payments.application.exception.PaymentsNotFoundException;
import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class GetPaymentByReferenceUseCase {
    private final PaymentRepository paymentRepository;

    public GetPaymentByReferenceUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment execute(UUID tenantId, String referenceType, String referenceId) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceType, "referenceType");
        Objects.requireNonNull(referenceId, "referenceId");
        return paymentRepository.findByReference(tenantId, referenceType, referenceId)
                .orElseThrow(() -> new PaymentsNotFoundException("payment not found"));
    }
}
