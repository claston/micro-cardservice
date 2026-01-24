package com.sistema.ledger.application;

import com.sistema.ledger.application.model.StatementPage;
import com.sistema.ledger.domain.repository.EntryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetAccountStatementUseCaseTest {

    @Test
    void shouldDefaultPaginationWhenInvalidValues() {
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);
        GetAccountStatementUseCase useCase = new GetAccountStatementUseCase(entryRepository);

        UUID accountId = UUID.randomUUID();
        StatementPage page = new StatementPage(accountId, Collections.emptyList(), 0, 20, 0);
        when(entryRepository.getStatement(accountId, null, null, 0, 20)).thenReturn(page);

        StatementPage result = useCase.execute(accountId, null, null, -1, 0);

        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
        verify(entryRepository).getStatement(accountId, null, null, 0, 20);
    }

    @Test
    void shouldPassFiltersToRepository() {
        EntryRepository entryRepository = Mockito.mock(EntryRepository.class);
        GetAccountStatementUseCase useCase = new GetAccountStatementUseCase(entryRepository);

        UUID accountId = UUID.randomUUID();
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-01-02T00:00:00Z");
        StatementPage page = new StatementPage(accountId, Collections.emptyList(), 1, 10, 0);
        when(entryRepository.getStatement(accountId, from, to, 1, 10)).thenReturn(page);

        StatementPage result = useCase.execute(accountId, from, to, 1, 10);

        assertEquals(1, result.getPage());
        assertEquals(10, result.getSize());
        verify(entryRepository).getStatement(accountId, from, to, 1, 10);
    }
}
