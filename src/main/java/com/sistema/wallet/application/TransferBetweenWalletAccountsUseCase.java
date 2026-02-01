package com.sistema.wallet.application;

import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.exception.WalletAccountNotFoundException;
import com.sistema.wallet.application.exception.WalletInsufficientBalanceException;
import com.sistema.wallet.application.model.WalletTransferResult;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class TransferBetweenWalletAccountsUseCase {
    private final WalletAccountRepository walletAccountRepository;
    private final GetAccountBalanceUseCase getAccountBalanceUseCase;
    private final PostLedgerTransactionUseCase postLedgerTransactionUseCase;

    public TransferBetweenWalletAccountsUseCase(WalletAccountRepository walletAccountRepository,
                                                GetAccountBalanceUseCase getAccountBalanceUseCase,
                                                PostLedgerTransactionUseCase postLedgerTransactionUseCase) {
        this.walletAccountRepository = walletAccountRepository;
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
        this.postLedgerTransactionUseCase = postLedgerTransactionUseCase;
    }

    @Transactional
    public WalletTransferResult execute(UUID tenantId, TransferBetweenWalletAccountsCommand command) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(command, "command");
        if (command.getAmountMinor() <= 0) {
            System.out.println("TransferBetweenWalletAccounts: invalid amount " + command.getAmountMinor());
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (command.getIdempotencyKey() == null || command.getIdempotencyKey().isBlank()) {
            System.out.println("TransferBetweenWalletAccounts: missing idempotencyKey");
            throw new IllegalArgumentException("idempotencyKey is required");
        }

        WalletAccount fromAccount = walletAccountRepository.findById(tenantId, command.getFromAccountId())
                .orElseThrow(() -> {
                    System.out.println("TransferBetweenWalletAccounts: fromAccount not found " + command.getFromAccountId());
                    return new WalletAccountNotFoundException(command.getFromAccountId());
                });
        WalletAccount toAccount = walletAccountRepository.findById(tenantId, command.getToAccountId())
                .orElseThrow(() -> {
                    System.out.println("TransferBetweenWalletAccounts: toAccount not found " + command.getToAccountId());
                    return new WalletAccountNotFoundException(command.getToAccountId());
                });

        if (!fromAccount.getCurrency().equals(command.getCurrency())
                || !toAccount.getCurrency().equals(command.getCurrency())) {
            System.out.println("TransferBetweenWalletAccounts: currency mismatch from=" + fromAccount.getCurrency()
                    + " to=" + toAccount.getCurrency() + " requested=" + command.getCurrency());
            throw new IllegalArgumentException("currency mismatch");
        }

        var fromBalance = getAccountBalanceUseCase.execute(tenantId, fromAccount.getLedgerAccountId());
        if (fromAccount.getOwnerType() != com.sistema.wallet.domain.model.WalletOwnerType.FUNDING
                && fromBalance.getBalanceMinor() < command.getAmountMinor()) {
            System.out.println("TransferBetweenWalletAccounts: insufficient balance " + fromBalance.getBalanceMinor()
                    + " < " + command.getAmountMinor());
            throw new WalletInsufficientBalanceException("insufficient balance");
        }

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

        try {
            var transaction = postLedgerTransactionUseCase.execute(ledgerCommand);
            return new WalletTransferResult(transaction.getId(), "POSTED");
        } catch (RuntimeException ex) {
            System.out.println("TransferBetweenWalletAccounts: ledger transfer failed idempotencyKey="
                    + command.getIdempotencyKey() + " message=" + ex.getMessage());
            throw ex;
        }
    }
}
