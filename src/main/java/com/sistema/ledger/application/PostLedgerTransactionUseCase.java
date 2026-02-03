package com.sistema.ledger.application;

import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.model.PostLedgerTransactionResult;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.IdempotencyKey;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.repository.EntryRepository;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
import com.sistema.ledger.domain.repository.LedgerTransactionRepository;
import com.sistema.ledger.application.rules.LedgerPostingRuleContext;
import com.sistema.ledger.application.rules.LedgerPostingRuleContextFactory;
import com.sistema.ledger.application.rules.PostingRulesPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PostLedgerTransactionUseCase {
    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerPostingRuleContextFactory ruleContextFactory;
    private final PostingRulesPipeline postingRulesPipeline;

    public PostLedgerTransactionUseCase(LedgerAccountRepository ledgerAccountRepository,
                                        EntryRepository entryRepository,
                                        LedgerTransactionRepository ledgerTransactionRepository) {
        this.ledgerTransactionRepository = ledgerTransactionRepository;
        this.ruleContextFactory = new LedgerPostingRuleContextFactory(ledgerAccountRepository);
        this.postingRulesPipeline = new PostingRulesPipeline(entryRepository);
    }

    @Transactional
    public LedgerTransaction execute(PostLedgerTransactionCommand command) {
        return executeInternal(command).getTransaction();
    }

    @Transactional
    public PostLedgerTransactionResult executeWithResult(PostLedgerTransactionCommand command) {
        return executeInternal(command);
    }

    private PostLedgerTransactionResult executeInternal(PostLedgerTransactionCommand command) {
        Objects.requireNonNull(command, "command");

        Optional<LedgerTransaction> existing =
                ledgerTransactionRepository.findByIdempotencyKey(command.getTenantId(), command.getIdempotencyKey());
        if (existing.isPresent()) {
            return new PostLedgerTransactionResult(existing.get(), true);
        }

        LedgerPostingRuleContext ruleContext = ruleContextFactory.build(command);
        postingRulesPipeline.validate(ruleContext);

        UUID transactionId = UUID.randomUUID();
        List<Entry> fixedEntries = ruleContext.getEntries().stream()
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
                ruleContext.getOccurredAt(),
                ruleContext.getCreatedAt(),
                fixedEntries
        );

        LedgerTransaction saved = ledgerTransactionRepository.save(transaction);
        return new PostLedgerTransactionResult(saved, false);
    }

}
