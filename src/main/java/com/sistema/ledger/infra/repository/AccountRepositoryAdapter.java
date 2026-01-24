package com.sistema.ledger.infra.repository;

import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.repository.AccountRepository;
import com.sistema.ledger.infra.entity.AccountEntity;
import com.sistema.ledger.infra.mapper.LedgerEntityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AccountRepositoryAdapter implements AccountRepository, PanacheRepository<AccountEntity> {
    @Override
    public Account save(Account account) {
        AccountEntity entity = LedgerEntityMapper.toEntity(account);
        persist(entity);
        getEntityManager().flush();
        return LedgerEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        AccountEntity entity = find("id", id).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(LedgerEntityMapper.toDomain(entity));
    }
}
