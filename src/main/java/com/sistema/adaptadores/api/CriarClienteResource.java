package com.sistema.adaptadores.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.casodeuso.CriarClienteUseCase;
import com.sistema.dominio.entidade.Customer;
import com.sistema.adaptadores.dto.ClienteDTO;
import com.sistema.infraestrutura.mapper.CustomerMapper;

import com.sistema.dominio.repository.CustomerRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CriarClienteResource {

    @Inject
    CriarClienteUseCase criarClienteUseCase;

    @Inject
    CustomerMapper customerMapper;

    @Inject
    CustomerRepository customerRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClientes(){
        List<Customer> customers = customerRepository.findAllAsList();

        if(customers.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(customers).build();
    }

    @POST
    public Response criarCliente(ClienteDTO clienteDTO){

        Customer customer = customerMapper.toDomain(clienteDTO);

        Customer customerCriado = criarClienteUseCase.executar(customer);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(customerCriado);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Cliente criado com sucesso.");
        return Response.status(Response.Status.CREATED)
                .entity(customerCriado)
                .build();
    }
}
