package com.sistema.wallet.application;

import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.IdempotencyKey;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.model.Money;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.exception.WalletAccountNotFoundException;
import com.sistema.wallet.application.exception.WalletInsufficientBalanceException;
import com.sistema.wallet.application.model.WalletTransferResult;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferBetweenWalletAccountsUseCaseTest {

    @Test
    void shouldTransferBetweenAccounts() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountBalanceUseCase getAccountBalanceUseCase = Mockito.mock(GetAccountBalanceUseCase.class);
        PostLedgerTransactionUseCase postLedgerTransactionUseCase = Mockito.mock(PostLedgerTransactionUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        UUID fromLedgerId = UUID.randomUUID();
        UUID toLedgerId = UUID.randomUUID();

        when(walletAccountRepository.findById(tenantId, fromWalletId))
                .thenReturn(Optional.of(walletAccount(fromWalletId, tenantId, fromLedgerId, "BRL")));
        when(walletAccountRepository.findById(tenantId, toWalletId))
                .thenReturn(Optional.of(walletAccount(toWalletId, tenantId, toLedgerId, "BRL")));

        when(getAccountBalanceUseCase.execute(tenantId, fromLedgerId))
                .thenReturn(new AccountBalance(fromLedgerId, 10000, "BRL"));

        LedgerTransaction ledgerTransaction = new LedgerTransaction(
                UUID.randomUUID(),
                tenantId,
                new IdempotencyKey("idemp-1"),
                null,
                "transfer",
                Instant.now(),
                Instant.now(),
                List.of(
                        entry(fromLedgerId, tenantId, EntryDirection.DEBIT, 5000),
                        entry(toLedgerId, tenantId, EntryDirection.CREDIT, 5000)
                )
        );
        when(postLedgerTransactionUseCase.execute(any())).thenReturn(ledgerTransaction);

        TransferBetweenWalletAccountsUseCase useCase =
                new TransferBetweenWalletAccountsUseCase(walletAccountRepository, getAccountBalanceUseCase, postLedgerTransactionUseCase);

        TransferBetweenWalletAccountsCommand command = new TransferBetweenWalletAccountsCommand(
                "idemp-1",
                fromWalletId,
                toWalletId,
                5000,
                "BRL",
                "transfer"
        );

        WalletTransferResult result = useCase.execute(tenantId, command);

        assertEquals(ledgerTransaction.getId(), result.getTransactionId());
        assertEquals("POSTED", result.getStatus());
    }

    @Test
    void shouldRejectInsufficientBalance() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountBalanceUseCase getAccountBalanceUseCase = Mockito.mock(GetAccountBalanceUseCase.class);
        PostLedgerTransactionUseCase postLedgerTransactionUseCase = Mockito.mock(PostLedgerTransactionUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        UUID fromLedgerId = UUID.randomUUID();
        UUID toLedgerId = UUID.randomUUID();

        when(walletAccountRepository.findById(tenantId, fromWalletId))
                .thenReturn(Optional.of(walletAccount(fromWalletId, tenantId, fromLedgerId, "BRL")));
        when(walletAccountRepository.findById(tenantId, toWalletId))
                .thenReturn(Optional.of(walletAccount(toWalletId, tenantId, toLedgerId, "BRL")));

        when(getAccountBalanceUseCase.execute(tenantId, fromLedgerId))
                .thenReturn(new AccountBalance(fromLedgerId, 100, "BRL"));

        TransferBetweenWalletAccountsUseCase useCase =
                new TransferBetweenWalletAccountsUseCase(walletAccountRepository, getAccountBalanceUseCase, postLedgerTransactionUseCase);

        TransferBetweenWalletAccountsCommand command = new TransferBetweenWalletAccountsCommand(
                "idemp-2",
                fromWalletId,
                toWalletId,
                5000,
                "BRL",
                "transfer"
        );

        assertThrows(WalletInsufficientBalanceException.class, () -> useCase.execute(tenantId, command));
        verify(postLedgerTransactionUseCase, never()).execute(any());
    }

    @Test
    void shouldRejectCurrencyMismatch() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountBalanceUseCase getAccountBalanceUseCase = Mockito.mock(GetAccountBalanceUseCase.class);
        PostLedgerTransactionUseCase postLedgerTransactionUseCase = Mockito.mock(PostLedgerTransactionUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        UUID fromLedgerId = UUID.randomUUID();
        UUID toLedgerId = UUID.randomUUID();

        when(walletAccountRepository.findById(tenantId, fromWalletId))
                .thenReturn(Optional.of(walletAccount(fromWalletId, tenantId, fromLedgerId, "BRL")));
        when(walletAccountRepository.findById(tenantId, toWalletId))
                .thenReturn(Optional.of(walletAccount(toWalletId, tenantId, toLedgerId, "USD")));

        TransferBetweenWalletAccountsUseCase useCase =
                new TransferBetweenWalletAccountsUseCase(walletAccountRepository, getAccountBalanceUseCase, postLedgerTransactionUseCase);

        TransferBetweenWalletAccountsCommand command = new TransferBetweenWalletAccountsCommand(
                "idemp-3",
                fromWalletId,
                toWalletId,
                5000,
                "BRL",
                "transfer"
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(tenantId, command));
        verify(postLedgerTransactionUseCase, never()).execute(any());
    }

    @Test
    void shouldRejectInvalidInput() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountBalanceUseCase getAccountBalanceUseCase = Mockito.mock(GetAccountBalanceUseCase.class);
        PostLedgerTransactionUseCase postLedgerTransactionUseCase = Mockito.mock(PostLedgerTransactionUseCase.class);

        TransferBetweenWalletAccountsUseCase useCase =
                new TransferBetweenWalletAccountsUseCase(walletAccountRepository, getAccountBalanceUseCase, postLedgerTransactionUseCase);

        TransferBetweenWalletAccountsCommand command = new TransferBetweenWalletAccountsCommand(
                " ",
                UUID.randomUUID(),
                UUID.randomUUID(),
                0,
                "BRL",
                "transfer"
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(UUID.randomUUID(), command));
        verify(postLedgerTransactionUseCase, never()).execute(any());
    }

    @Test
    void shouldRejectWhenWalletAccountMissing() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountBalanceUseCase getAccountBalanceUseCase = Mockito.mock(GetAccountBalanceUseCase.class);
        PostLedgerTransactionUseCase postLedgerTransactionUseCase = Mockito.mock(PostLedgerTransactionUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();

        when(walletAccountRepository.findById(tenantId, fromWalletId)).thenReturn(Optional.empty());

        TransferBetweenWalletAccountsUseCase useCase =
                new TransferBetweenWalletAccountsUseCase(walletAccountRepository, getAccountBalanceUseCase, postLedgerTransactionUseCase);

        TransferBetweenWalletAccountsCommand command = new TransferBetweenWalletAccountsCommand(
                "idemp-4",
                fromWalletId,
                toWalletId,
                5000,
                "BRL",
                "transfer"
        );

        assertThrows(WalletAccountNotFoundException.class, () -> useCase.execute(tenantId, command));
        verify(postLedgerTransactionUseCase, never()).execute(any());
    }

    private WalletAccount walletAccount(UUID walletId, UUID tenantId, UUID ledgerAccountId, String currency) {
        return new WalletAccount(
                walletId,
                tenantId,
                WalletOwnerType.CUSTOMER,
                "user",
                currency,
                WalletAccountStatus.ACTIVE,
                null,
                ledgerAccountId,
                Instant.now()
        );
    }

    private Entry entry(UUID ledgerAccountId, UUID tenantId, EntryDirection direction, long amountMinor) {
        return new Entry(
                UUID.randomUUID(),
                tenantId,
                UUID.randomUUID(),
                ledgerAccountId,
                direction,
                new Money(amountMinor, "BRL"),
                Instant.now(),
                Instant.now()
        );
    }
}
