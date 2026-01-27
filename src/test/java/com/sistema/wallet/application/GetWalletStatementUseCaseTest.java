package com.sistema.wallet.application;

import com.sistema.ledger.application.GetAccountStatementUseCase;
import com.sistema.ledger.application.model.StatementItem;
import com.sistema.ledger.application.model.StatementPage;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.wallet.application.exception.WalletAccountNotFoundException;
import com.sistema.wallet.application.model.WalletStatementPage;
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
import static org.mockito.Mockito.when;

class GetWalletStatementUseCaseTest {

    @Test
    void shouldReturnStatementFromLedger() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountStatementUseCase getAccountStatementUseCase = Mockito.mock(GetAccountStatementUseCase.class);

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

        StatementPage ledgerStatement = new StatementPage(
                ledgerAccountId,
                List.of(new StatementItem(
                        Instant.parse("2026-01-20T10:00:00Z"),
                        UUID.randomUUID(),
                        "Transfer",
                        EntryDirection.DEBIT,
                        5000,
                        "BRL"
                )),
                0,
                20,
                1
        );

        when(getAccountStatementUseCase.execute(tenantId, ledgerAccountId, null, null, 0, 20))
                .thenReturn(ledgerStatement);

        GetWalletStatementUseCase useCase = new GetWalletStatementUseCase(walletAccountRepository, getAccountStatementUseCase);
        WalletStatementPage statement = useCase.execute(tenantId, walletAccountId, null, null, 0, 20);

        assertEquals(walletAccountId, statement.getAccountId());
        assertEquals(1, statement.getItems().size());
        assertEquals("DEBIT", statement.getItems().get(0).getDirection());
    }

    @Test
    void shouldThrowWhenWalletAccountNotFound() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        GetAccountStatementUseCase getAccountStatementUseCase = Mockito.mock(GetAccountStatementUseCase.class);

        UUID tenantId = UUID.randomUUID();
        UUID walletAccountId = UUID.randomUUID();

        when(walletAccountRepository.findById(tenantId, walletAccountId)).thenReturn(Optional.empty());

        GetWalletStatementUseCase useCase = new GetWalletStatementUseCase(walletAccountRepository, getAccountStatementUseCase);

        assertThrows(WalletAccountNotFoundException.class, () -> useCase.execute(tenantId, walletAccountId, null, null, 0, 20));
    }
}
