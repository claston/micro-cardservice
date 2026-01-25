package com.sistema.creditcard.adaptadores.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.creditcard.adaptadores.dto.TransacaoDTO;
import com.sistema.creditcard.casodeuso.RegistarTransacaoNaFaturaUseCase;
import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.dominio.entidade.Transacao;
import com.sistema.creditcard.infraestrutura.mapper.TransacaoMapper;
import com.sistema.creditcard.dominio.repository.FaturaRepository;
import com.sistema.creditcard.infraestrutura.repositorio.TransacaoRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Optional;

@Path("/fatura")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FaturaResource {

    @Inject
    TransacaoMapper transacaoMapper;

    @Inject
    FaturaRepository faturaRepository;

    @Inject
    TransacaoRepository transacaoRepository;

    @Inject
    RegistarTransacaoNaFaturaUseCase registarTransacaoNaFaturaUseCase;

    @GET
    @Path("/{ano}/{mes}")
    public Response getFatura(@PathParam("ano") int ano, @PathParam("mes") int mes){
        LocalDate mesAno = LocalDate.of(ano, mes, 1);
        Optional<Fatura> faturaOpt = faturaRepository.findByMesAno(mesAno);
        return faturaOpt.map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @POST
    @Path("/transacao")
    @Transactional
    public Response adicionalTransacao(TransacaoDTO transacaoDTO) {

        Transacao transacao = transacaoMapper.toDomain(transacaoDTO);

        Fatura fatura = registarTransacaoNaFaturaUseCase.registarTransacaoNaFatura(transacao);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(fatura);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(Response.Status.CREATED).entity(fatura).build();
    }

    @POST
    @Path("/pagar/{ano}/{mes}")
    @Transactional
    public Response pagarFatura(@PathParam("ano") int ano, @PathParam("mes") int mes) {

        LocalDate mesAno = LocalDate.of(ano, mes, 1);
        Optional<Fatura> faturaOpt = faturaRepository.findByMesAno(mesAno);

        if (faturaOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Fatura não encontrada").build();
        }

        Fatura fatura = faturaOpt.get();
        fatura.setPaga(true);
        return Response.ok().entity("Fatura paga com Sucesso!").build();
    }
}


