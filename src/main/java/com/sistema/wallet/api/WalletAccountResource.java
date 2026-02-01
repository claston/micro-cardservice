package com.sistema.wallet.api;

import com.sistema.wallet.api.dto.CreateWalletAccountRequest;
import com.sistema.wallet.api.dto.CreateWalletAccountResponse;
import com.sistema.wallet.api.dto.WalletBalanceResponse;
import com.sistema.wallet.api.dto.WalletStatementItemResponse;
import com.sistema.wallet.api.dto.WalletStatementResponse;
import com.sistema.customer.application.GetCustomerUseCase;
import com.sistema.customer.application.exception.CustomerNotFoundException;
import com.sistema.wallet.application.CreateWalletAccountUseCase;
import com.sistema.wallet.application.GetWalletBalanceUseCase;
import com.sistema.wallet.application.GetWalletStatementUseCase;
import com.sistema.wallet.application.command.CreateWalletAccountCommand;
import com.sistema.wallet.application.model.WalletStatementPage;
import com.sistema.common.tenant.TenantResolver;
import com.sistema.wallet.application.exception.WalletOwnerNotFoundException;
import com.sistema.wallet.application.exception.WalletUnauthorizedException;
import com.sistema.wallet.domain.model.WalletOwnerType;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WalletAccountResource {
    @Inject
    TenantResolver tenantResolver;

    @Inject
    CreateWalletAccountUseCase createWalletAccountUseCase;

    @Inject
    GetWalletBalanceUseCase getWalletBalanceUseCase;

    @Inject
    GetWalletStatementUseCase getWalletStatementUseCase;

    @Inject
    GetCustomerUseCase getCustomerUseCase;

    @POST
    public Response createAccount(@HeaderParam("X-API-Key") String apiKey,
                                  @Valid CreateWalletAccountRequest request) {
        UUID tenantId = requireTenantId(apiKey);
        WalletOwnerType ownerType = WalletOwnerType.valueOf(request.getOwnerType());
        if (ownerType == WalletOwnerType.CUSTOMER) {
            validateCustomerExists(tenantId, request.getOwnerId());
        }
        var command = new CreateWalletAccountCommand(
                ownerType,
                request.getOwnerId(),
                request.getCurrency(),
                request.getLabel()
        );
        var walletAccount = createWalletAccountUseCase.execute(tenantId, command);
        CreateWalletAccountResponse response = new CreateWalletAccountResponse();
        response.setAccountId(walletAccount.getId().toString());
        response.setOwnerType(walletAccount.getOwnerType().name());
        response.setOwnerId(walletAccount.getOwnerId());
        response.setCurrency(walletAccount.getCurrency());
        response.setStatus(walletAccount.getStatus().name());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{accountId}/balance")
    public WalletBalanceResponse getBalance(@HeaderParam("X-API-Key") String apiKey,
                                            @PathParam("accountId") UUID accountId) {
        UUID tenantId = requireTenantId(apiKey);
        var balance = getWalletBalanceUseCase.execute(tenantId, accountId);
        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setAccountId(balance.getAccountId().toString());
        response.setBalanceMinor(balance.getBalanceMinor());
        response.setCurrency(balance.getCurrency());
        return response;
    }

    @GET
    @Path("/{accountId}/statement")
    public WalletStatementResponse getStatement(@HeaderParam("X-API-Key") String apiKey,
                                                @PathParam("accountId") UUID accountId,
                                                @QueryParam("from") Instant from,
                                                @QueryParam("to") Instant to,
                                                @QueryParam("page") Integer page,
                                                @QueryParam("size") Integer size) {
        UUID tenantId = requireTenantId(apiKey);
        WalletStatementPage statement = getWalletStatementUseCase.execute(
                tenantId,
                accountId,
                from,
                to,
                page == null ? 0 : page,
                size == null ? 20 : size
        );

        WalletStatementResponse response = new WalletStatementResponse();
        response.setAccountId(statement.getAccountId().toString());
        response.setPage(statement.getPage());
        response.setSize(statement.getSize());
        response.setTotal(statement.getTotal());
        List<WalletStatementItemResponse> items = statement.getItems().stream()
                .map(item -> {
                    WalletStatementItemResponse itemResponse = new WalletStatementItemResponse();
                    itemResponse.setTransactionId(item.getTransactionId());
                    itemResponse.setOccurredAt(item.getOccurredAt());
                    itemResponse.setDescription(item.getDescription());
                    itemResponse.setDirection(item.getDirection());
                    itemResponse.setAmountMinor(item.getAmountMinor());
                    itemResponse.setCurrency(item.getCurrency());
                    return itemResponse;
                })
                .collect(Collectors.toList());
        response.setItems(items);
        return response;
    }

    private UUID requireTenantId(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new WalletUnauthorizedException("apiKey is required");
        }
        return tenantResolver.resolveTenantId(apiKey)
                .orElseThrow(() -> new WalletUnauthorizedException("apiKey not recognized"));
    }

    private void validateCustomerExists(UUID tenantId, String ownerId) {
        UUID customerId;
        try {
            customerId = UUID.fromString(ownerId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("ownerId must be a valid UUID for CUSTOMER");
        }
        try {
            getCustomerUseCase.execute(tenantId, customerId);
        } catch (CustomerNotFoundException ex) {
            throw new WalletOwnerNotFoundException(ownerId);
        }
    }
}
