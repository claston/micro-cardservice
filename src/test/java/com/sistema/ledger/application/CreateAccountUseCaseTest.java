package com.sistema.ledger.application;

import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.domain.model.Account;
import com.sistema.ledger.domain.model.AccountStatus;
import com.sistema.ledger.domain.model.AccountType;
import com.sistema.ledger.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateAccountUseCaseTest {

    @Test
    void shouldCreateAccountWithActiveStatus() {
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountUseCase useCase = new CreateAccountUseCase(accountRepository);
        CreateAccountCommand command = new CreateAccountCommand(
                "Carteira Cliente",
                AccountType.ASSET,
                "BRL",
                false
        );

        Account account = useCase.execute(command);

        assertNotNull(account.getId());
        assertEquals("Carteira Cliente", account.getName());
        assertEquals(AccountType.ASSET, account.getType());
        assertEquals("BRL", account.getCurrency());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }
}
