package com.sistema.wallet.application;

import com.sistema.ledger.application.CreateAccountUseCase;
import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.wallet.application.command.CreateWalletAccountCommand;
import com.sistema.wallet.application.exception.WalletAccountAlreadyExistsException;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletAccountStatus;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateWalletAccountUseCaseTest {

    @Test
    void shouldCreateWalletAccountWhenUnique() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        CreateAccountUseCase createAccountUseCase = Mockito.mock(CreateAccountUseCase.class);

        UUID tenantId = UUID.randomUUID();
        CreateWalletAccountCommand command = new CreateWalletAccountCommand(
                WalletOwnerType.CUSTOMER,
                "user-123",
                "BRL",
                "Main Wallet"
        );

        when(walletAccountRepository.findByOwner(tenantId, WalletOwnerType.CUSTOMER, "user-123", "BRL"))
                .thenReturn(Optional.empty());

        UUID ledgerAccountId = UUID.randomUUID();
        when(createAccountUseCase.execute(any(CreateAccountCommand.class)))
                .thenReturn(new LedgerAccount(
                        ledgerAccountId,
                        tenantId,
                        "Ledger Wallet",
                        AccountType.LIABILITY,
                        "BRL",
                        false,
                        AccountStatus.ACTIVE,
                        Instant.now()
                ));

        when(walletAccountRepository.save(any(WalletAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateWalletAccountUseCase useCase = new CreateWalletAccountUseCase(walletAccountRepository, createAccountUseCase);

        WalletAccount walletAccount = useCase.execute(tenantId, command);

        assertNotNull(walletAccount.getId());
        assertEquals(tenantId, walletAccount.getTenantId());
        assertEquals(WalletOwnerType.CUSTOMER, walletAccount.getOwnerType());
        assertEquals("user-123", walletAccount.getOwnerId());
        assertEquals("BRL", walletAccount.getCurrency());
        assertEquals(WalletAccountStatus.ACTIVE, walletAccount.getStatus());
        assertEquals("Main Wallet", walletAccount.getLabel());
        assertEquals(ledgerAccountId, walletAccount.getLedgerAccountId());

        ArgumentCaptor<CreateAccountCommand> commandCaptor = ArgumentCaptor.forClass(CreateAccountCommand.class);
        verify(createAccountUseCase).execute(commandCaptor.capture());
        CreateAccountCommand ledgerCommand = commandCaptor.getValue();
        assertEquals(tenantId, ledgerCommand.getTenantId());
        assertEquals(AccountType.LIABILITY, ledgerCommand.getType());
        assertEquals("BRL", ledgerCommand.getCurrency());
        assertEquals(false, ledgerCommand.isAllowNegative());
    }

    @Test
    void shouldRejectDuplicateWalletAccount() {
        WalletAccountRepository walletAccountRepository = Mockito.mock(WalletAccountRepository.class);
        CreateAccountUseCase createAccountUseCase = Mockito.mock(CreateAccountUseCase.class);

        UUID tenantId = UUID.randomUUID();
        CreateWalletAccountCommand command = new CreateWalletAccountCommand(
                WalletOwnerType.CUSTOMER,
                "user-123",
                "BRL",
                null
        );

        WalletAccount existing = new WalletAccount(
                UUID.randomUUID(),
                tenantId,
                WalletOwnerType.CUSTOMER,
                "user-123",
                "BRL",
                WalletAccountStatus.ACTIVE,
                null,
                UUID.randomUUID(),
                Instant.now()
        );
        when(walletAccountRepository.findByOwner(tenantId, WalletOwnerType.CUSTOMER, "user-123", "BRL"))
                .thenReturn(Optional.of(existing));

        CreateWalletAccountUseCase useCase = new CreateWalletAccountUseCase(walletAccountRepository, createAccountUseCase);

        assertThrows(WalletAccountAlreadyExistsException.class, () -> useCase.execute(tenantId, command));
        verify(createAccountUseCase, never()).execute(any(CreateAccountCommand.class));
    }
}
