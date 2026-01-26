package com.sistema.ledger.api;

import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.api.dto.LedgerEntryRequest;
import com.sistema.ledger.api.dto.PostLedgerTransactionRequest;
import com.sistema.ledger.api.dto.PostLedgerTransactionResponse;
import com.sistema.ledger.domain.model.Entry;
import com.sistema.ledger.domain.model.EntryDirection;
import com.sistema.ledger.domain.model.IdempotencyKey;
import com.sistema.ledger.domain.model.LedgerTransaction;
import com.sistema.ledger.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import jakarta.ws.rs.WebApplicationException;

public class LedgerTransactionResourceTest {

    @Test
    public void shouldPostTransaction() {
        PostLedgerTransactionUseCase postLedgerTransactionUseCase = mock(PostLedgerTransactionUseCase.class);
        LedgerTransactionResource resource = new LedgerTransactionResource();
        resource.postLedgerTransactionUseCase = postLedgerTransactionUseCase;

        UUID tenantId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Instant now = Instant.parse("2026-01-02T12:00:00Z");
        Entry debitEntry = new Entry(
                UUID.randomUUID(),
                tenantId,
                transactionId,
                accountId,
                EntryDirection.DEBIT,
                new Money(1000L, "BRL"),
                now,
                now
        );
        Entry creditEntry = new Entry(
                UUID.randomUUID(),
                tenantId,
                transactionId,
                accountId,
                EntryDirection.CREDIT,
                new Money(1000L, "BRL"),
                now,
                now
        );
        LedgerTransaction transaction = new LedgerTransaction(
                transactionId,
                tenantId,
                new IdempotencyKey("idemp-1"),
                "ext-1",
                "test",
                now,
                now,
                List.of(debitEntry, creditEntry)
        );
        when(postLedgerTransactionUseCase.execute(any())).thenReturn(transaction);

        PostLedgerTransactionRequest request = new PostLedgerTransactionRequest();
        request.setIdempotencyKey("idemp-1");
        request.setExternalReference("ext-1");
        request.setDescription("test");
        request.setOccurredAt(now);
        List<LedgerEntryRequest> entries = new ArrayList<>();
        entries.add(entryRequest(accountId, "DEBIT", 1000L, "BRL"));
        entries.add(entryRequest(accountId, "CREDIT", 1000L, "BRL"));
        request.setEntries(entries);

        var response = resource.postTransaction(tenantId, request);
        assertEquals(201, response.getStatus());
        PostLedgerTransactionResponse body = (PostLedgerTransactionResponse) response.getEntity();
        assertEquals(transactionId, body.getTransactionId());
    }

    @Test
    public void shouldRejectInvalidDirection() {
        PostLedgerTransactionUseCase postLedgerTransactionUseCase = mock(PostLedgerTransactionUseCase.class);
        LedgerTransactionResource resource = new LedgerTransactionResource();
        resource.postLedgerTransactionUseCase = postLedgerTransactionUseCase;

        UUID tenantId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        PostLedgerTransactionRequest request = new PostLedgerTransactionRequest();
        request.setIdempotencyKey("idemp-2");
        List<LedgerEntryRequest> entries = new ArrayList<>();
        entries.add(entryRequest(accountId, "INVALID", 1000L, "BRL"));
        entries.add(entryRequest(accountId, "CREDIT", 1000L, "BRL"));
        request.setEntries(entries);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> resource.postTransaction(tenantId, request));
        assertEquals(400, ex.getResponse().getStatus());
    }

    private LedgerEntryRequest entryRequest(UUID accountId, String direction, long amountMinor, String currency) {
        LedgerEntryRequest request = new LedgerEntryRequest();
        request.setAccountId(accountId);
        request.setDirection(direction);
        request.setAmountMinor(amountMinor);
        request.setCurrency(currency);
        return request;
    }
}
