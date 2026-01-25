package com.sistema.ledger.infra.repository;

import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
import com.sistema.ledger.infra.entity.LedgerAccountEntity;
import com.sistema.ledger.infra.mapper.LedgerEntityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class LedgerAccountRepositoryAdapter implements LedgerAccountRepository, PanacheRepository<LedgerAccountEntity> {
    @Override
    public LedgerAccount save(LedgerAccount ledgerAccount) {
        LedgerAccountEntity entity = LedgerEntityMapper.toEntity(ledgerAccount);
        persist(entity);
        getEntityManager().flush();
        return LedgerEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<LedgerAccount> findById(UUID tenantId, UUID id) {
        LedgerAccountEntity entity = find("tenantId = ?1 and id = ?2", tenantId, id).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(LedgerEntityMapper.toDomain(entity));
    }
}
