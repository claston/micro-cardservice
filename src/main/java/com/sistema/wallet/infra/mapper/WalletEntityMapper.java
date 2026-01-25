package com.sistema.wallet.infra.mapper;

import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.infra.entity.WalletAccountEntity;

public final class WalletEntityMapper {
    private WalletEntityMapper() {
    }

    public static WalletAccountEntity toEntity(WalletAccount walletAccount) {
        WalletAccountEntity entity = new WalletAccountEntity();
        entity.setId(walletAccount.getId());
        entity.setTenantId(walletAccount.getTenantId());
        entity.setOwnerType(walletAccount.getOwnerType().name());
        entity.setOwnerId(walletAccount.getOwnerId());
        entity.setCurrency(walletAccount.getCurrency());
        entity.setStatus(walletAccount.getStatus().name());
        entity.setLabel(walletAccount.getLabel());
        entity.setLedgerAccountId(walletAccount.getLedgerAccountId());
        entity.setCreatedAt(walletAccount.getCreatedAt());
        return entity;
    }

    public static WalletAccount toDomain(WalletAccountEntity entity) {
        return new WalletAccount(
                entity.getId(),
                entity.getTenantId(),
                WalletOwnerType.valueOf(entity.getOwnerType()),
                entity.getOwnerId(),
                entity.getCurrency(),
                WalletAccountStatus.valueOf(entity.getStatus()),
                entity.getLabel(),
                entity.getLedgerAccountId(),
                entity.getCreatedAt()
        );
    }
}
