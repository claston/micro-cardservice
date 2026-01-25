package com.sistema.wallet.infra.repository;

import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import com.sistema.wallet.infra.entity.WalletAccountEntity;
import com.sistema.wallet.infra.mapper.WalletEntityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class WalletAccountRepositoryAdapter implements WalletAccountRepository, PanacheRepository<WalletAccountEntity> {
    @Override
    public WalletAccount save(WalletAccount walletAccount) {
        WalletAccountEntity entity = WalletEntityMapper.toEntity(walletAccount);
        persist(entity);
        getEntityManager().flush();
        return WalletEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<WalletAccount> findById(UUID tenantId, UUID accountId) {
        WalletAccountEntity entity = find("tenantId = ?1 and id = ?2", tenantId, accountId).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(WalletEntityMapper.toDomain(entity));
    }

    @Override
    public Optional<WalletAccount> findByOwner(UUID tenantId, WalletOwnerType ownerType, String ownerId, String currency) {
        WalletAccountEntity entity = find(
                "tenantId = ?1 and ownerType = ?2 and ownerId = ?3 and currency = ?4",
                tenantId,
                ownerType.name(),
                ownerId,
                currency
        ).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(WalletEntityMapper.toDomain(entity));
    }
}
