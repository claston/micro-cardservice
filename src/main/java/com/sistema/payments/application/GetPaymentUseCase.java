package com.sistema.payments.application;

import com.sistema.payments.application.exception.PaymentsNotFoundException;
import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class GetPaymentUseCase {
    private final PaymentRepository paymentRepository;

    public GetPaymentUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment execute(UUID tenantId, UUID paymentId) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(paymentId, "paymentId");
        return paymentRepository.findById(tenantId, paymentId)
                .orElseThrow(() -> new PaymentsNotFoundException("payment not found"));
    }
}
