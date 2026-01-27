package com.sistema.wallet.application;

import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.wallet.application.exception.WalletAccountNotFoundException;
import com.sistema.wallet.application.model.WalletBalance;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetWalletBalanceUseCaseTest {

    @Test
    void shouldReturnBalanceForWalletAccount() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountBalanceUseCase getAccountBalanceUseCase = Mockito.mock(GetAccountBalanceUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID walletAccountId = UUID.randomUUID();
        UUID ledgerAccountId = UUID.randomUUID();

        WalletAccount walletAccount = new WalletAccount(
                walletAccountId,
                tenantId,
                WalletOwnerType.CUSTOMER,
                "user-1",
                "BRL",
                WalletAccountStatus.ACTIVE,
                null,
                ledgerAccountId,
                Instant.now()
        );

        when(walletAccountRepository.findById(tenantId, walletAccountId)).thenReturn(Optional.of(walletAccount));
        when(getAccountBalanceUseCase.execute(tenantId, ledgerAccountId))
                .thenReturn(new AccountBalance(ledgerAccountId, 2500L, "BRL"));

        GetWalletBalanceUseCase useCase = new GetWalletBalanceUseCase(walletAccountRepository, getAccountBalanceUseCase);
        WalletBalance balance = useCase.execute(tenantId, walletAccountId);

        assertEquals(walletAccountId, balance.getAccountId());
        assertEquals(2500L, balance.getBalanceMinor());
        assertEquals("BRL", balance.getCurrency());
    }

    @Test
    void shouldThrowWhenWalletAccountNotFound() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountBalanceUseCase getAccountBalanceUseCase = Mockito.mock(GetAccountBalanceUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID walletAccountId = UUID.randomUUID();

        when(walletAccountRepository.findById(tenantId, walletAccountId)).thenReturn(Optional.empty());

        GetWalletBalanceUseCase useCase = new GetWalletBalanceUseCase(walletAccountRepository, getAccountBalanceUseCase);

        assertThrows(WalletAccountNotFoundException.class, () -> useCase.execute(tenantId, walletAccountId));
    }
}
