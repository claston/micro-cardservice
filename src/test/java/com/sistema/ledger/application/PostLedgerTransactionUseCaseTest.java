package com.sistema.ledger.application;

import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.IdempotencyKey;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.repository.AccountRepository;
import com.sistema.ledger.domain.repository.EntryRepository;
import com.sistema.ledger.domain.repository.LedgerTransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostLedgerTransactionUseCaseTest {

    @Test
    void shouldReturnExistingTransactionForIdempotencyKey() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);
        LedgerTransactionRepository transactionRepository = Mockito.mock(LedgerTransactionRepository.class);

        LedgerTransaction existing = new LedgerTransaction(
                UUID.randomUUID(),
                new IdempotencyKey("idemp-1"),
                "ext-1",
                "existing",
                Instant.now(),
                Instant.now(),
                List.of(sampleEntry(EntryDirection.DEBIT), sampleEntry(EntryDirection.CREDIT))
        );
        when(transactionRepository.findByIdempotencyKey("idemp-1")).thenReturn(Optional.of(existing));

        PostLedgerTransactionUseCase useCase =
                new PostLedgerTransactionUseCase(accountRepository, entryRepository, transactionRepository);

        PostLedgerTransactionCommand command = new PostLedgerTransactionCommand(
                "idemp-1",
                "ext-1",
                "ignored",
                Instant.now(),
                List.of(sampleEntryCommand())
        );

        LedgerTransaction result = useCase.execute(command);

        assertEquals(existing.getId(), result.getId());
        verify(transactionRepository, never()).save(any(LedgerTransaction.class));
    }

    @Test
    void shouldFailWhenDoubleEntryDoesNotClose() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);
        LedgerTransactionRepository transactionRepository = Mockito.mock(LedgerTransactionRepository.class);

        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account(accountId, "BRL", false)));
        when(entryRepository.getBalanceMinor(accountId)).thenReturn(0L);
        when(transactionRepository.findByIdempotencyKey("idemp-2")).thenReturn(Optional.empty());

        PostLedgerTransactionUseCase useCase =
                new PostLedgerTransactionUseCase(accountRepository, entryRepository, transactionRepository);

        PostLedgerTransactionCommand command = new PostLedgerTransactionCommand(
                "idemp-2",
                "ext-2",
                "invalid",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(accountId, EntryDirection.DEBIT, 1000, "BRL"),
                        new PostingEntryCommand(accountId, EntryDirection.CREDIT, 900, "BRL")
                )
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        verify(transactionRepository, never()).save(any(LedgerTransaction.class));
    }

    @Test
    void shouldFailWhenNegativeBalanceNotAllowed() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);
        LedgerTransactionRepository transactionRepository = Mockito.mock(LedgerTransactionRepository.class);

        UUID debitAccount = UUID.randomUUID();
        UUID creditAccount = UUID.randomUUID();
        when(accountRepository.findById(debitAccount))
                .thenReturn(Optional.of(account(debitAccount, "BRL", false)));
        when(accountRepository.findById(creditAccount))
                .thenReturn(Optional.of(account(creditAccount, "BRL", true)));
        when(entryRepository.getBalanceMinor(debitAccount)).thenReturn(0L);
        when(entryRepository.getBalanceMinor(creditAccount)).thenReturn(0L);
        when(transactionRepository.findByIdempotencyKey("idemp-3")).thenReturn(Optional.empty());

        PostLedgerTransactionUseCase useCase =
                new PostLedgerTransactionUseCase(accountRepository, entryRepository, transactionRepository);

        PostLedgerTransactionCommand command = new PostLedgerTransactionCommand(
                "idemp-3",
                "ext-3",
                "negative",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(debitAccount, EntryDirection.DEBIT, 1000, "BRL"),
                        new PostingEntryCommand(creditAccount, EntryDirection.CREDIT, 1000, "BRL")
                )
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        verify(transactionRepository, never()).save(any(LedgerTransaction.class));
    }

    @Test
    void shouldPostTransactionWhenValid() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);
        LedgerTransactionRepository transactionRepository = Mockito.mock(LedgerTransactionRepository.class);

        UUID debitAccount = UUID.randomUUID();
        UUID creditAccount = UUID.randomUUID();
        when(accountRepository.findById(debitAccount))
                .thenReturn(Optional.of(account(debitAccount, "BRL", true)));
        when(accountRepository.findById(creditAccount))
                .thenReturn(Optional.of(account(creditAccount, "BRL", true)));
        when(entryRepository.getBalanceMinor(debitAccount)).thenReturn(0L);
        when(entryRepository.getBalanceMinor(creditAccount)).thenReturn(0L);
        when(transactionRepository.findByIdempotencyKey("idemp-4")).thenReturn(Optional.empty());
        when(transactionRepository.save(any(LedgerTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostLedgerTransactionUseCase useCase =
                new PostLedgerTransactionUseCase(accountRepository, entryRepository, transactionRepository);

        PostLedgerTransactionCommand command = new PostLedgerTransactionCommand(
                "idemp-4",
                "ext-4",
                "valid",
                Instant.now(),
                List.of(
                        new PostingEntryCommand(debitAccount, EntryDirection.DEBIT, 1000, "BRL"),
                        new PostingEntryCommand(creditAccount, EntryDirection.CREDIT, 1000, "BRL")
                )
        );

        LedgerTransaction result = useCase.execute(command);

        assertNotNull(result.getId());
        assertEquals(2, result.getEntries().size());
        verify(transactionRepository).save(any(LedgerTransaction.class));
    }

    private Account account(UUID id, String currency, boolean allowNegative) {
        return new Account(
                id,
                "Conta Teste",
                AccountType.ASSET,
                currency,
                allowNegative,
                AccountStatus.ACTIVE,
                Instant.now()
        );
    }

    private PostingEntryCommand sampleEntryCommand() {
        return new PostingEntryCommand(
                UUID.randomUUID(),
                EntryDirection.DEBIT,
                1000,
                "BRL"
        );
    }

    private com.sistema.ledger.domain.model.Entry sampleEntry(EntryDirection direction) {
        return new com.sistema.ledger.domain.model.Entry(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                direction,
                new com.sistema.ledger.domain.model.Money(1000, "BRL"),
                Instant.now(),
                Instant.now()
        );
    }
}
