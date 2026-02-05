package com.sistema.payments.domain.repository;

import java.time.Instant;
import java.util.UUID;

public interface PaymentWebhookEventRepository {
    boolean registerEvent(UUID tenantId, String externalPaymentId, String eventType, Instant receivedAt);
}
