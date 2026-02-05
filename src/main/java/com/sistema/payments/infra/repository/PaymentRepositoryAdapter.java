package com.sistema.payments.infra.repository;

import com.sistema.payments.domain.model.Payment;
import com.sistema.payments.domain.repository.PaymentRepository;
import com.sistema.payments.infra.entity.PaymentEntity;
import com.sistema.payments.infra.mapper.PaymentEntityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentRepositoryAdapter implements PaymentRepository, PanacheRepository<PaymentEntity> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntityMapper.toEntity(payment);
        PaymentEntity existing = entityManager.find(PaymentEntity.class, entity.getId());
        if (existing == null) {
            persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        entityManager.flush();
        return PaymentEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<Payment> findById(UUID tenantId, UUID paymentId) {
        PaymentEntity entity = find("tenantId = ?1 and id = ?2", tenantId, paymentId).firstResult();
        return entity == null ? Optional.empty() : Optional.of(PaymentEntityMapper.toDomain(entity));
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(UUID tenantId, String idempotencyKey) {
        PaymentEntity entity = find("tenantId = ?1 and idempotencyKey = ?2", tenantId, idempotencyKey).firstResult();
        return entity == null ? Optional.empty() : Optional.of(PaymentEntityMapper.toDomain(entity));
    }

    @Override
    public Optional<Payment> findByExternalPaymentId(UUID tenantId, String externalPaymentId) {
        PaymentEntity entity = find("tenantId = ?1 and externalPaymentId = ?2", tenantId, externalPaymentId).firstResult();
        return entity == null ? Optional.empty() : Optional.of(PaymentEntityMapper.toDomain(entity));
    }

    @Override
    public Optional<Payment> findByExternalPaymentId(String externalPaymentId) {
        PaymentEntity entity = find("externalPaymentId = ?1", externalPaymentId).firstResult();
        return entity == null ? Optional.empty() : Optional.of(PaymentEntityMapper.toDomain(entity));
    }

    @Override
    public Optional<Payment> findByReference(UUID tenantId, String referenceType, String referenceId) {
        PaymentEntity entity = find(
                "tenantId = ?1 and referenceType = ?2 and referenceId = ?3",
                tenantId,
                referenceType,
                referenceId
        ).firstResult();
        return entity == null ? Optional.empty() : Optional.of(PaymentEntityMapper.toDomain(entity));
    }
}
