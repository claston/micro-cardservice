package com.sistema.ledger.infra.repository;

import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.repository.LedgerTransactionRepository;
import com.sistema.ledger.infra.entity.AccountEntity;
import com.sistema.ledger.infra.entity.EntryEntity;
import com.sistema.ledger.infra.entity.LedgerTransactionEntity;
import com.sistema.ledger.infra.mapper.LedgerEntityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class LedgerTransactionRepositoryAdapter implements LedgerTransactionRepository, PanacheRepository<LedgerTransactionEntity> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<LedgerTransaction> findByIdempotencyKey(UUID tenantId, String idempotencyKey) {
        LedgerTransactionEntity transactionEntity = find("tenantId = ?1 and idempotencyKey = ?2", tenantId, idempotencyKey).firstResult();
        if (transactionEntity == null) {
            return Optional.empty();
        }
        List<EntryEntity> entryEntities = entityManager.createQuery(
                        "from EntryEntity e where e.tenantId = :tenantId and e.transaction.id = :transactionId order by e.occurredAt asc",
                        EntryEntity.class)
                .setParameter("tenantId", tenantId)
                .setParameter("transactionId", transactionEntity.getId())
                .getResultList();
        List<Entry> entries = entryEntities.stream()
                .map(LedgerEntityMapper::toDomain)
                .collect(Collectors.toList());
        return Optional.of(LedgerEntityMapper.toDomain(transactionEntity, entries));
    }

    @Override
    public LedgerTransaction save(LedgerTransaction transaction) {
        LedgerTransactionEntity transactionEntity = new LedgerTransactionEntity();
        transactionEntity.setId(transaction.getId());
        transactionEntity.setTenantId(transaction.getTenantId());
        transactionEntity.setIdempotencyKey(transaction.getIdempotencyKey().getValue());
        transactionEntity.setExternalReference(transaction.getExternalReference());
        transactionEntity.setDescription(transaction.getDescription());
        transactionEntity.setOccurredAt(transaction.getOccurredAt());
        transactionEntity.setCreatedAt(transaction.getCreatedAt());
        persist(transactionEntity);

        for (Entry entry : transaction.getEntries()) {
            EntryEntity entryEntity = new EntryEntity();
            entryEntity.setId(entry.getId());
            entryEntity.setTenantId(entry.getTenantId());
            entryEntity.setTransaction(transactionEntity);
            AccountEntity accountRef = entityManager.getReference(AccountEntity.class, entry.getAccountId());
            entryEntity.setAccount(accountRef);
            entryEntity.setDirection(entry.getDirection().name());
            entryEntity.setAmountMinor(entry.getMoney().getAmountMinor());
            entryEntity.setCurrency(entry.getMoney().getCurrency());
            entryEntity.setOccurredAt(entry.getOccurredAt());
            entryEntity.setCreatedAt(entry.getCreatedAt());
            entityManager.persist(entryEntity);
        }
        entityManager.flush();
        return transaction;
    }
}
