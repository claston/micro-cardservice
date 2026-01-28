package com.sistema.customer.api;

import com.sistema.common.tenant.TenantResolver;
import com.sistema.customer.api.dto.CreateCustomerRequest;
import com.sistema.customer.api.dto.CustomerResponse;
import com.sistema.customer.api.dto.CustomerSearchResponse;
import com.sistema.customer.application.CreateCustomerUseCase;
import com.sistema.customer.application.GetCustomerUseCase;
import com.sistema.customer.application.SearchCustomerByDocumentUseCase;
import com.sistema.customer.application.command.CreateCustomerCommand;
import com.sistema.customer.application.exception.CustomerUnauthorizedException;
import com.sistema.customer.domain.model.CustomerDocumentType;
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

import java.util.List;
import java.util.UUID;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
    @Inject
    TenantResolver tenantResolver;

    @Inject
    CreateCustomerUseCase createCustomerUseCase;

    @Inject
    GetCustomerUseCase getCustomerUseCase;

    @Inject
    SearchCustomerByDocumentUseCase searchCustomerByDocumentUseCase;

    @POST
    public Response createCustomer(@HeaderParam("X-API-Key") String apiKey,
                                   @Valid CreateCustomerRequest request) {
        UUID tenantId = requireTenantId(apiKey);
        CreateCustomerCommand command = new CreateCustomerCommand(
                request.getType(),
                request.getName(),
                request.getDocumentType(),
                request.getDocumentNumber()
        );
        var customer = createCustomerUseCase.execute(tenantId, command);
        return Response.status(Response.Status.CREATED).entity(CustomerResponse.from(customer)).build();
    }

    @GET
    @Path("/{customerId}")
    public CustomerResponse getCustomer(@HeaderParam("X-API-Key") String apiKey,
                                        @PathParam("customerId") UUID customerId) {
        UUID tenantId = requireTenantId(apiKey);
        var customer = getCustomerUseCase.execute(tenantId, customerId);
        return CustomerResponse.from(customer);
    }

    @GET
    public CustomerSearchResponse searchByDocument(@HeaderParam("X-API-Key") String apiKey,
                                                   @QueryParam("documentType") String documentType,
                                                   @QueryParam("documentNumber") String documentNumber,
                                                   @QueryParam("page") Integer page,
                                                   @QueryParam("size") Integer size) {
        UUID tenantId = requireTenantId(apiKey);
        int resolvedPage = page == null ? 0 : page;
        int resolvedSize = size == null ? 20 : size;
        if (documentType == null || documentType.isBlank() || documentNumber == null || documentNumber.isBlank()) {
            CustomerSearchResponse response = new CustomerSearchResponse();
            response.setItems(List.of());
            response.setPage(resolvedPage);
            response.setSize(resolvedSize);
            response.setTotal(0);
            return response;
        }

        CustomerDocumentType docType = CustomerDocumentType.valueOf(documentType);
        var found = searchCustomerByDocumentUseCase.execute(tenantId, docType, documentNumber);

        CustomerSearchResponse response = new CustomerSearchResponse();
        response.setPage(resolvedPage);
        response.setSize(resolvedSize);
        if (found.isPresent() && resolvedPage == 0) {
            response.setItems(List.of(CustomerResponse.from(found.get())));
            response.setTotal(1);
        } else {
            response.setItems(List.of());
            response.setTotal(found.isPresent() ? 1 : 0);
        }
        return response;
    }

    private UUID requireTenantId(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new CustomerUnauthorizedException("apiKey is required");
        }
        return tenantResolver.resolveTenantId(apiKey)
                .orElseThrow(() -> new CustomerUnauthorizedException("apiKey not recognized"));
    }
}


