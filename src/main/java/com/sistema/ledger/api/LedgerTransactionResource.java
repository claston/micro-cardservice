package com.sistema.ledger.api;

import com.sistema.ledger.api.dto.LedgerEntryRequest;
import com.sistema.ledger.api.dto.PostLedgerTransactionRequest;
import com.sistema.ledger.api.dto.PostLedgerTransactionResponse;
import com.sistema.ledger.application.PostLedgerTransactionUseCase;
import com.sistema.ledger.application.command.PostLedgerTransactionCommand;
import com.sistema.ledger.application.command.PostingEntryCommand;
import com.sistema.ledger.domain.model.EntryDirection;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/ledger/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LedgerTransactionResource {
    @Inject
    PostLedgerTransactionUseCase postLedgerTransactionUseCase;

    @POST
    public Response postTransaction(PostLedgerTransactionRequest request) {
        try {
            List<PostingEntryCommand> entries = request.getEntries().stream()
                    .map(this::toCommand)
                    .collect(Collectors.toList());
            PostLedgerTransactionCommand command = new PostLedgerTransactionCommand(
                    request.getIdempotencyKey(),
                    request.getExternalReference(),
                    request.getDescription(),
                    request.getOccurredAt(),
                    entries
            );
            var transaction = postLedgerTransactionUseCase.execute(command);
            return Response.status(Response.Status.CREATED)
                    .entity(new PostLedgerTransactionResponse(transaction.getId()))
                    .build();
        } catch (IllegalArgumentException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    private PostingEntryCommand toCommand(LedgerEntryRequest entry) {
        return new PostingEntryCommand(
                entry.getAccountId(),
                EntryDirection.valueOf(entry.getDirection()),
                entry.getAmountMinor(),
                entry.getCurrency()
        );
    }
}
