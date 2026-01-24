package com.sistema.ledger.application;

import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.repository.AccountRepository;
import com.sistema.ledger.domain.repository.EntryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetAccountBalanceUseCaseTest {

    @Test
    void shouldReturnBalanceForExistingAccount() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);

        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account(accountId)));
        when(entryRepository.getBalanceMinor(accountId)).thenReturn(2500L);

        GetAccountBalanceUseCase useCase = new GetAccountBalanceUseCase(accountRepository, entryRepository);
        AccountBalance balance = useCase.execute(accountId);

        assertEquals(accountId, balance.getAccountId());
        assertEquals(2500L, balance.getBalanceMinor());
        assertEquals("BRL", balance.getCurrency());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);

        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        GetAccountBalanceUseCase useCase = new GetAccountBalanceUseCase(accountRepository, entryRepository);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(accountId));
    }

    private Account account(UUID id) {
        return new Account(
                id,
                "Conta Teste",
                AccountType.ASSET,
                "BRL",
                false,
                AccountStatus.ACTIVE,
                Instant.now()
        );
    }
}
