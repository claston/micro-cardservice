package com.sistema.wallet.application;

import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.exception.WalletIdempotencyConflictException;
import com.sistema.wallet.application.model.WalletTransferResult;
import com.sistema.wallet.application.rules.WalletTransferRuleContext;
import com.sistema.wallet.application.rules.WalletTransferRuleContextFactory;
import com.sistema.wallet.application.rules.WalletTransferRulesPipeline;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.validation.WalletTransferPolicy;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class TransferBetweenWalletAccountsUseCase {
    private final PostLedgerTransactionUseCase postLedgerTransactionUseCase;
    private final WalletTransferRuleContextFactory ruleContextFactory;
    private final WalletTransferRulesPipeline rulesPipeline;

    public TransferBetweenWalletAccountsUseCase(WalletAccountRepository walletAccountRepository,
                                                GetAccountBalanceUseCase getAccountBalanceUseCase,
                                                PostLedgerTransactionUseCase postLedgerTransactionUseCase) {
        this.postLedgerTransactionUseCase = postLedgerTransactionUseCase;
        this.ruleContextFactory = new WalletTransferRuleContextFactory(walletAccountRepository);
        this.rulesPipeline = new WalletTransferRulesPipeline(getAccountBalanceUseCase);
    }

    @Transactional
    public WalletTransferResult execute(UUID tenantId, TransferBetweenWalletAccountsCommand command) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(command, "command");
        WalletTransferPolicy policy = new WalletTransferPolicy();
        policy.validateAmountPositive(command.getAmountMinor());
        policy.validateIdempotencyKey(command.getIdempotencyKey());
        WalletTransferRuleContext context = ruleContextFactory.build(tenantId, command);
        rulesPipeline.validate(context);

        WalletAccount fromAccount = context.getFromAccount();
        WalletAccount toAccount = context.getToAccount();

        PostLedgerTransactionCommand ledgerCommand = new PostLedgerTransactionCommand(
                tenantId,
                command.getIdempotencyKey(),
                null,
                command.getDescription(),
                Instant.now(),
                List.of(
                        new PostingEntryCommand(fromAccount.getLedgerAccountId(), EntryDirection.DEBIT, command.getAmountMinor(), command.getCurrency()),
                        new PostingEntryCommand(toAccount.getLedgerAccountId(), EntryDirection.CREDIT, command.getAmountMinor(), command.getCurrency())
                )
        );

        var result = postLedgerTransactionUseCase.executeWithResult(ledgerCommand);
        if (result.isIdempotentReplay()) {
            throw new WalletIdempotencyConflictException(
                    command.getIdempotencyKey(),
                    result.getTransaction().getId()
            );
        }
        return new WalletTransferResult(result.getTransaction().getId(), "POSTED");
    }
}
