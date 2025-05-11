package com.sistema.adaptadores.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.casodeuso.RegistarCompraCasoDeUso;
import com.sistema.dominio.entidade.Transacao;
import com.sistema.adaptadores.dto.TransacaoDTO;
import com.sistema.infraestrutura.mapper.TransacaoMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/transacoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransacaoResource {

    @Inject
    RegistarCompraCasoDeUso registarCompraCasoDeUso;

    @Inject
    TransacaoMapper transacaoMapper;

    @POST
    public Response registrarCompra(TransacaoDTO transacaoDTO){

        try {
            Transacao transacao = transacaoMapper.toDomain(transacaoDTO);

            Transacao transacaoCriada = registarCompraCasoDeUso.registraCompra(transacao);

            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                String json = mapper.writeValueAsString(transacaoCriada);
                System.out.println(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Response.status(Response.Status.CREATED).entity(transacaoCriada).build();


        } catch (IllegalArgumentException e){

            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }


    }

}
