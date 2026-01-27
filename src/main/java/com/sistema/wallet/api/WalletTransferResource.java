package com.sistema.wallet.api;

import com.sistema.wallet.api.dto.TransferRequest;
import com.sistema.wallet.api.dto.TransferResponse;
import com.sistema.wallet.application.TransferBetweenWalletAccountsUseCase;
import com.sistema.wallet.application.command.TransferBetweenWalletAccountsCommand;
import com.sistema.wallet.application.tenant.TenantResolver;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WalletTransferResource {
    @Inject
    TenantResolver tenantResolver;

    @Inject
    TransferBetweenWalletAccountsUseCase transferBetweenWalletAccountsUseCase;

    @POST
    public Response transfer(@HeaderParam("X-API-Key") String apiKey,
                             @Valid TransferRequest request) {
        UUID tenantId = requireTenantId(apiKey);
        TransferBetweenWalletAccountsCommand command = new TransferBetweenWalletAccountsCommand(
                request.getIdempotencyKey(),
                UUID.fromString(request.getFromAccountId()),
                UUID.fromString(request.getToAccountId()),
                request.getAmountMinor(),
                request.getCurrency(),
                request.getDescription()
        );
        var result = transferBetweenWalletAccountsUseCase.execute(tenantId, command);
        TransferResponse response = new TransferResponse();
        response.setTransactionId(result.getTransactionId().toString());
        response.setStatus(result.getStatus());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    private UUID requireTenantId(String apiKey) {
        return tenantResolver.resolveTenantId(apiKey);
    }
}
