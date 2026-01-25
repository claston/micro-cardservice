package com.sistema.ledger.application;

import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.IdempotencyKey;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.model.Money;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
import com.sistema.ledger.domain.repository.EntryRepository;
import com.sistema.ledger.domain.repository.LedgerTransactionRepository;
import com.sistema.ledger.domain.validation.DoubleEntryValidator;
import com.sistema.ledger.domain.validation.NegativeBalancePolicy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PostLedgerTransactionUseCase {
    private final LedgerAccountRepository ledgerAccountRepository;
    private final EntryRepository entryRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;

    public PostLedgerTransactionUseCase(LedgerAccountRepository ledgerAccountRepository,
                                        EntryRepository entryRepository,
                                        LedgerTransactionRepository ledgerTransactionRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
        this.entryRepository = entryRepository;
        this.ledgerTransactionRepository = ledgerTransactionRepository;
    }

    @Transactional
    public LedgerTransaction execute(PostLedgerTransactionCommand command) {
        Objects.requireNonNull(command, "command");

        Optional<LedgerTransaction> existing =
                ledgerTransactionRepository.findByIdempotencyKey(command.getTenantId(), command.getIdempotencyKey());
        if (existing.isPresent()) {
            return existing.get();
        }

        if (command.getEntries() == null || command.getEntries().size() < 2) {
            throw new IllegalArgumentException("entries must have at least 2 items");
        }

        Map<UUID, LedgerAccount> accountsById = loadAccounts(command.getTenantId(), command.getEntries());
        Instant occurredAt = command.getOccurredAt() != null ? command.getOccurredAt() : Instant.now();
        Instant createdAt = Instant.now();

        List<Entry> entries = new ArrayList<>();
        for (PostingEntryCommand entryCommand : command.getEntries()) {
            LedgerAccount ledgerAccount = accountsById.get(entryCommand.getLedgerAccountId());
            if (!ledgerAccount.getTenantId().equals(command.getTenantId())) {
                throw new IllegalArgumentException("cross-tenant operation is not allowed");
            }
            String currency = entryCommand.getCurrency() == null ? ledgerAccount.getCurrency() : entryCommand.getCurrency();
            if (!ledgerAccount.getCurrency().equals(currency)) {
                throw new IllegalArgumentException("currency mismatch for account " + ledgerAccount.getId());
            }

            Money money = new Money(entryCommand.getAmountMinor(), currency);
            Entry entry = new Entry(
                    UUID.randomUUID(),
                    command.getTenantId(),
                    UUID.randomUUID(), // temporary, replaced below
                    ledgerAccount.getId(),
                    entryCommand.getDirection(),
                    money,
                    occurredAt,
                    createdAt
            );
            entries.add(entry);
        }

        DoubleEntryValidator.validate(entries);
        validateNegativeBalances(command.getTenantId(), accountsById, entries);

        UUID transactionId = UUID.randomUUID();
        List<Entry> fixedEntries = entries.stream()
                .map(entry -> new Entry(
                        entry.getId(),
                        entry.getTenantId(),
                        transactionId,
                        entry.getLedgerAccountId(),
                        entry.getDirection(),
                        entry.getMoney(),
                        entry.getOccurredAt(),
                        entry.getCreatedAt()
                ))
                .toList();

        LedgerTransaction transaction = new LedgerTransaction(
                transactionId,
                command.getTenantId(),
                new IdempotencyKey(command.getIdempotencyKey()),
                command.getExternalReference(),
                command.getDescription(),
                occurredAt,
                createdAt,
                fixedEntries
        );

        return ledgerTransactionRepository.save(transaction);
    }

    private Map<UUID, LedgerAccount> loadAccounts(UUID tenantId, List<PostingEntryCommand> entries) {
        Map<UUID, LedgerAccount> accountsById = new HashMap<>();
        for (PostingEntryCommand entryCommand : entries) {
            UUID ledgerAccountId = entryCommand.getLedgerAccountId();
            if (accountsById.containsKey(ledgerAccountId)) {
                continue;
            }
            LedgerAccount ledgerAccount = ledgerAccountRepository.findById(tenantId, ledgerAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("account not found: " + ledgerAccountId));
            if (ledgerAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new IllegalArgumentException("account not active: " + ledgerAccountId);
            }
            if (!ledgerAccount.getTenantId().equals(tenantId)) {
                throw new IllegalArgumentException("cross-tenant operation is not allowed");
            }
            accountsById.put(ledgerAccountId, ledgerAccount);
        }
        return accountsById;
    }

    private void validateNegativeBalances(UUID tenantId, Map<UUID, LedgerAccount> accountsById, List<Entry> entries) {
        Map<UUID, Long> deltaByAccount = new HashMap<>();
        for (Entry entry : entries) {
            long delta = entry.getDirection() == EntryDirection.CREDIT
                    ? entry.getMoney().getAmountMinor()
                    : -entry.getMoney().getAmountMinor();
            deltaByAccount.merge(entry.getLedgerAccountId(), delta, Long::sum);
        }

        for (Map.Entry<UUID, Long> delta : deltaByAccount.entrySet()) {
            LedgerAccount account = accountsById.get(delta.getKey());
            long currentBalance = entryRepository.getBalanceMinor(tenantId, account.getId());
            long resultingBalance = currentBalance + delta.getValue();
            NegativeBalancePolicy.check(account.isAllowNegative(), resultingBalance);
        }
    }
}
