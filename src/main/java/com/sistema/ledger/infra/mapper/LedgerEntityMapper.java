package com.sistema.ledger.infra.mapper;

import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.IdempotencyKey;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.model.Money;
import com.sistema.ledger.infra.entity.LedgerAccountEntity;
import com.sistema.ledger.infra.entity.EntryEntity;
import com.sistema.ledger.infra.entity.LedgerTransactionEntity;

import java.util.List;

public final class LedgerEntityMapper {
    private LedgerEntityMapper() {
    }

    public static LedgerAccountEntity toEntity(LedgerAccount ledgerAccount) {
        LedgerAccountEntity entity = new LedgerAccountEntity();
        entity.setId(ledgerAccount.getId());
        entity.setTenantId(ledgerAccount.getTenantId());
        entity.setName(ledgerAccount.getName());
        entity.setType(ledgerAccount.getType().name());
        entity.setCurrency(ledgerAccount.getCurrency());
        entity.setAllowNegative(ledgerAccount.isAllowNegative());
        entity.setStatus(ledgerAccount.getStatus().name());
        entity.setCreatedAt(ledgerAccount.getCreatedAt());
        return entity;
    }

    public static LedgerAccount toDomain(LedgerAccountEntity entity) {
        return new LedgerAccount(
                entity.getId(),
                entity.getTenantId(),
                entity.getName(),
                AccountType.valueOf(entity.getType()),
                entity.getCurrency(),
                entity.isAllowNegative(),
                AccountStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }

    public static LedgerTransaction toDomain(LedgerTransactionEntity transactionEntity, List<Entry> entries) {
        return new LedgerTransaction(
                transactionEntity.getId(),
                transactionEntity.getTenantId(),
                new IdempotencyKey(transactionEntity.getIdempotencyKey()),
                transactionEntity.getExternalReference(),
                transactionEntity.getDescription(),
                transactionEntity.getOccurredAt(),
                transactionEntity.getCreatedAt(),
                entries
        );
    }

    public static Entry toDomain(EntryEntity entity) {
        return new Entry(
                entity.getId(),
                entity.getTenantId(),
                entity.getTransaction().getId(),
                entity.getLedgerAccount().getId(),
                EntryDirection.valueOf(entity.getDirection()),
                new Money(entity.getAmountMinor(), entity.getCurrency()),
                entity.getOccurredAt(),
                entity.getCreatedAt()
        );
    }
}
