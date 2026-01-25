package com.sistema.wallet.domain.repository;

import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletOwnerType;

import java.util.Optional;
import java.util.UUID;

public interface WalletAccountRepository {
    WalletAccount save(WalletAccount walletAccount);

    Optional<WalletAccount> findById(UUID tenantId, UUID accountId);

    Optional<WalletAccount> findByOwner(UUID tenantId, WalletOwnerType ownerType, String ownerId, String currency);
}
