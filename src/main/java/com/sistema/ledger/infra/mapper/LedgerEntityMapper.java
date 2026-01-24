package com.sistema.ledger.infra.mapper;

import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.IdempotencyKey;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.model.Money;
import com.sistema.ledger.infra.entity.AccountEntity;
import com.sistema.ledger.infra.entity.EntryEntity;
import com.sistema.ledger.infra.entity.LedgerTransactionEntity;

import java.util.List;

public final class LedgerEntityMapper {
    private LedgerEntityMapper() {
    }

    public static AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setName(account.getName());
        entity.setType(account.getType().name());
        entity.setCurrency(account.getCurrency());
        entity.setAllowNegative(account.isAllowNegative());
        entity.setStatus(account.getStatus().name());
        entity.setCreatedAt(account.getCreatedAt());
        return entity;
    }

    public static Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
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
                entity.getTransaction().getId(),
                entity.getAccount().getId(),
                EntryDirection.valueOf(entity.getDirection()),
                new Money(entity.getAmountMinor(), entity.getCurrency()),
                entity.getOccurredAt(),
                entity.getCreatedAt()
        );
    }
}
