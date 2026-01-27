package com.sistema.customer.api;

import com.sistema.customer.api.dto.CustomerDTO;
import com.sistema.customer.application.CreateCustomerUseCase;
import com.sistema.customer.application.ListCustomersUseCase;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.infra.mapper.CustomerMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CreateCustomerUseCase createCustomerUseCase;

    @Inject
    CustomerMapper customerMapper;

    @Inject
    ListCustomersUseCase listCustomersUseCase;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        List<Customer> customers = listCustomersUseCase.execute();

        if (customers.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(customers).build();
    }

    @POST
    public Response createCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toDomain(customerDTO);
        Customer customerCriado = createCustomerUseCase.execute(customer);

        return Response.status(Response.Status.CREATED)
                .entity(customerCriado)
                .build();
    }
}


