package com.sistema.adaptadores.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.casodeuso.CriarClienteUseCase;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.adaptadores.dto.ClienteDTO;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.mapper.ClienteMapper;

import com.sistema.infraestrutura.repositorio.ClienteRepository;
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
    ClienteMapper clienteMapper;

    @Inject
    ClienteRepository clienteRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClientes(){
        List<ClienteEntity> clientes = clienteRepository.findAllAsList();

        if(clientes.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(clientes).build();
    }

    @POST
    public Response criarCliente(ClienteDTO clienteDTO){

        Cliente cliente = clienteMapper.toDomain(clienteDTO);

        Cliente clienteCriado = criarClienteUseCase.executar(cliente);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(clienteCriado);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Cliente criado com sucesso.");
        return Response.status(Response.Status.CREATED)
                .entity(clienteCriado)
                .build();
    }
}
