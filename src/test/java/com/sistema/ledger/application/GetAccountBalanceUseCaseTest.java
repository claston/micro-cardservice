package com.sistema.ledger.application;

import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
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
        LedgerAccountRepository ledgerAccountRepository = Mockito.mock(LedgerAccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);

        UUID tenantId = UUID.randomUUID();
        UUID ledgerAccountId = UUID.randomUUID();
        when(ledgerAccountRepository.findById(tenantId, ledgerAccountId))
                .thenReturn(Optional.of(account(tenantId, ledgerAccountId)));
        when(entryRepository.getBalanceMinor(tenantId, ledgerAccountId)).thenReturn(2500L);

        GetAccountBalanceUseCase useCase = new GetAccountBalanceUseCase(ledgerAccountRepository, entryRepository);
        AccountBalance balance = useCase.execute(tenantId, ledgerAccountId);

        assertEquals(ledgerAccountId, balance.getLedgerAccountId());
        assertEquals(2500L, balance.getBalanceMinor());
        assertEquals("BRL", balance.getCurrency());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        LedgerAccountRepository ledgerAccountRepository = Mockito.mock(LedgerAccountRepository.class);
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);

        UUID tenantId = UUID.randomUUID();
        UUID ledgerAccountId = UUID.randomUUID();
        when(ledgerAccountRepository.findById(tenantId, ledgerAccountId)).thenReturn(Optional.empty());

        GetAccountBalanceUseCase useCase = new GetAccountBalanceUseCase(ledgerAccountRepository, entryRepository);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(tenantId, ledgerAccountId));
    }

    private LedgerAccount account(UUID tenantId, UUID id) {
        return new LedgerAccount(
                id,
                tenantId,
                "Conta Teste",
                AccountType.ASSET,
                "BRL",
                false,
                AccountStatus.ACTIVE,
                Instant.now()
        );
    }
}
