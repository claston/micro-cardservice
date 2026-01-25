package com.sistema.ledger.infra.repository;

import com.sistema.ledger.application.model.StatementItem;
import com.sistema.ledger.application.model.StatementPage;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.repository.EntryRepository;
import com.sistema.ledger.infra.entity.EntryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class EntryRepositoryAdapter implements EntryRepository, PanacheRepository<EntryEntity> {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public long getBalanceMinor(UUID tenantId, UUID accountId) {
        Long balance = entityManager.createQuery(
                        "select coalesce(sum(case when e.direction = 'CREDIT' then e.amountMinor else -e.amountMinor end), 0) " +
                                "from EntryEntity e where e.tenantId = :tenantId and e.account.id = :accountId",
                        Long.class)
                .setParameter("tenantId", tenantId)
                .setParameter("accountId", accountId)
                .getSingleResult();
        return balance == null ? 0L : balance;
    }

    @Override
    public StatementPage getStatement(UUID tenantId, UUID accountId, Instant from, Instant to, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder("from EntryEntity e where e.tenantId = :tenantId and e.account.id = :accountId");
        if (from != null) {
            queryBuilder.append(" and e.occurredAt >= :from");
        }
        if (to != null) {
            queryBuilder.append(" and e.occurredAt <= :to");
        }
        queryBuilder.append(" order by e.occurredAt desc, e.id desc");

        var query = entityManager.createQuery(queryBuilder.toString(), EntryEntity.class)
                .setParameter("tenantId", tenantId)
                .setParameter("accountId", accountId)
                .setFirstResult(page * size)
                .setMaxResults(size);
        if (from != null) {
            query.setParameter("from", from);
        }
        if (to != null) {
            query.setParameter("to", to);
        }

        List<EntryEntity> results = query.getResultList();
        List<StatementItem> items = results.stream()
                .map(entity -> new StatementItem(
                        entity.getOccurredAt(),
                        entity.getTransaction().getId(),
                        entity.getTransaction().getDescription(),
                        EntryDirection.valueOf(entity.getDirection()),
                        entity.getAmountMinor(),
                        entity.getCurrency()))
                .collect(Collectors.toList());

        StringBuilder countBuilder = new StringBuilder("select count(e) from EntryEntity e where e.tenantId = :tenantId and e.account.id = :accountId");
        if (from != null) {
            countBuilder.append(" and e.occurredAt >= :from");
        }
        if (to != null) {
            countBuilder.append(" and e.occurredAt <= :to");
        }
        var countQuery = entityManager.createQuery(countBuilder.toString(), Long.class)
                .setParameter("tenantId", tenantId)
                .setParameter("accountId", accountId);
        if (from != null) {
            countQuery.setParameter("from", from);
        }
        if (to != null) {
            countQuery.setParameter("to", to);
        }

        long total = countQuery.getSingleResult();
        return new StatementPage(accountId, items, page, size, total);
    }
}
