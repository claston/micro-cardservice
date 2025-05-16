package com.sistema.adaptadores.api;

import com.sistema.adaptadores.dto.CreditCardDTO;
import com.sistema.casodeuso.CriarCartaoUseCase;
import com.sistema.dominio.entidade.CreditCard;

import com.sistema.infraestrutura.mapper.CartaoDeCreditoMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cartoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CriarCartaoResource {

    @Inject
    CriarCartaoUseCase criarCartaoDeCredito;

    @Inject
    CartaoDeCreditoMapper cartaoDeCreditoMapper;

    @POST
    public Response criarCartao(CreditCardDTO dto){

        CreditCard cartaoCriado = criarCartaoDeCredito.executar(dto);

        return Response.status(Response.Status.CREATED)
                .entity(cartaoCriado)
                .build();

    }
}
