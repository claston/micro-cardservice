package com.sistema.ledger.application;

import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.domain.model.LedgerAccount;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.repository.LedgerAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateAccountUseCaseTest {

    @Test
    void shouldCreateAccountWithActiveStatus() {
        LedgerAccountRepository ledgerAccountRepository = Mockito.mock(LedgerAccountRepository.class);
        when(ledgerAccountRepository.save(any(LedgerAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountUseCase useCase = new CreateAccountUseCase(ledgerAccountRepository);
        java.util.UUID tenantId = java.util.UUID.randomUUID();
        CreateAccountCommand command = new CreateAccountCommand(
                tenantId,
                "Carteira Cliente",
                AccountType.ASSET,
                "BRL",
                false
        );

        LedgerAccount ledgerAccount = useCase.execute(command);

        assertNotNull(ledgerAccount.getId());
        assertEquals(tenantId, ledgerAccount.getTenantId());
        assertEquals("Carteira Cliente", ledgerAccount.getName());
        assertEquals(AccountType.ASSET, ledgerAccount.getType());
        assertEquals("BRL", ledgerAccount.getCurrency());
        assertEquals(AccountStatus.ACTIVE, ledgerAccount.getStatus());
    }
}
