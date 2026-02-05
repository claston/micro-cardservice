package com.sistema.payments.infra.repository;

import com.sistema.payments.domain.repository.PaymentWebhookEventRepository;
import com.sistema.payments.infra.entity.PaymentWebhookEventEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class PaymentWebhookEventRepositoryAdapter implements PaymentWebhookEventRepository, PanacheRepository<PaymentWebhookEventEntity> {
    @Override
    public boolean registerEvent(UUID tenantId, String externalPaymentId, String eventType, Instant receivedAt) {
        PaymentWebhookEventEntity entity = new PaymentWebhookEventEntity();
        entity.setId(UUID.randomUUID());
        entity.setTenantId(tenantId);
        entity.setExternalPaymentId(externalPaymentId);
        entity.setEventType(eventType);
        entity.setReceivedAt(receivedAt);
        try {
            persist(entity);
            getEntityManager().flush();
            return true;
        } catch (PersistenceException ex) {
            return false;
        }
    }
}
