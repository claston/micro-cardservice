package com.sistema.ledger.api;

import com.sistema.ledger.api.dto.AccountBalanceResponse;
import com.sistema.ledger.api.dto.CreateAccountRequest;
import com.sistema.ledger.api.dto.CreateAccountResponse;
import com.sistema.ledger.api.dto.StatementItemResponse;
import com.sistema.ledger.api.dto.StatementResponse;
import com.sistema.ledger.application.CreateAccountUseCase;
import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.ledger.application.GetAccountStatementUseCase;
import com.sistema.ledger.application.command.CreateAccountCommand;
import com.sistema.ledger.application.model.AccountBalance;
import com.sistema.ledger.application.model.StatementPage;
import com.sistema.ledger.domain.model.AccountType;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/ledger/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LedgerAccountResource {
    @Inject
    CreateAccountUseCase createAccountUseCase;

    @Inject
    GetAccountBalanceUseCase getAccountBalanceUseCase;

    @Inject
    GetAccountStatementUseCase getAccountStatementUseCase;

    @POST
    public Response createAccount(@HeaderParam("X-Tenant-Id") UUID tenantId, CreateAccountRequest request) {
        try {
            UUID resolvedTenantId = requireTenantId(tenantId);
            AccountType type = AccountType.valueOf(request.getType());
            var command = new CreateAccountCommand(
                    resolvedTenantId,
                    request.getName(),
                    type,
                    request.getCurrency(),
                    request.isAllowNegative()
            );
            var ledgerAccount = createAccountUseCase.execute(command);
            return Response.status(Response.Status.CREATED)
                    .entity(new CreateAccountResponse(ledgerAccount.getId()))
                    .build();
        } catch (IllegalArgumentException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{id}/balance")
    public AccountBalanceResponse getBalance(@HeaderParam("X-Tenant-Id") UUID tenantId,
                                             @PathParam("id") UUID ledgerAccountId) {
        try {
            AccountBalance balance = getAccountBalanceUseCase.execute(requireTenantId(tenantId), ledgerAccountId);
            return new AccountBalanceResponse(balance.getLedgerAccountId(), balance.getBalanceMinor(), balance.getCurrency());
        } catch (IllegalArgumentException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("/{id}/statement")
    public StatementResponse getStatement(@HeaderParam("X-Tenant-Id") UUID tenantId,
                                          @PathParam("id") UUID ledgerAccountId,
                                          @QueryParam("from") Instant from,
                                          @QueryParam("to") Instant to,
                                          @QueryParam("page") Integer page,
                                          @QueryParam("size") Integer size) {
        StatementPage pageResult = getAccountStatementUseCase.execute(
                requireTenantId(tenantId),
                ledgerAccountId,
                from,
                to,
                page == null ? 0 : page,
                size == null ? 20 : size
        );
        StatementResponse response = new StatementResponse();
        response.setAccountId(pageResult.getLedgerAccountId());
        response.setPage(pageResult.getPage());
        response.setSize(pageResult.getSize());
        response.setTotal(pageResult.getTotal());
        List<StatementItemResponse> items = pageResult.getItems().stream()
                .map(item -> {
                    StatementItemResponse responseItem = new StatementItemResponse();
                    responseItem.setOccurredAt(item.getOccurredAt());
                    responseItem.setTransactionId(item.getTransactionId());
                    responseItem.setDescription(item.getDescription());
                    responseItem.setDirection(item.getDirection().name());
                    responseItem.setAmountMinor(item.getAmountMinor());
                    responseItem.setCurrency(item.getCurrency());
                    return responseItem;
                })
                .collect(Collectors.toList());
        response.setItems(items);
        return response;
    }

    private UUID requireTenantId(UUID tenantId) {
        if (tenantId == null) {
            throw new WebApplicationException("tenantId is required", Response.Status.BAD_REQUEST);
        }
        return tenantId;
    }
}
