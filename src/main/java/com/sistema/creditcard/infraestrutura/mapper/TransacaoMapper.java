package com.sistema.creditcard.infraestrutura.mapper;

import com.sistema.creditcard.adaptadores.dto.TransacaoDTO;
import com.sistema.creditcard.dominio.entidade.Transacao;
import com.sistema.creditcard.infraestrutura.entidade.TransacaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface TransacaoMapper {

    @Mapping(target = "cartao.id", source = "cartao.id")
    @Mapping(target = "fatura", ignore = true)
    @Mapping(target = "id", source = "id")
    TransacaoEntity toEntity(Transacao transacao);

    @Mapping(target = "cartao.id", source = "cartao.id")
    @Mapping(target = "fatura", ignore = true)
    @Mapping(target = "id", source = "id")
    Transacao toDomain(TransacaoEntity transacaoEntityEntity);

    @Mapping(target = "cartao.id", source = "cartaoId")
    @Mapping(target = "fatura", ignore = true)
    @Mapping(target = "id", ignore = true)
    Transacao toDomain(TransacaoDTO transacaoDTO);

    @Mapping(target = "cartaoId", source = "cartao.id")
    TransacaoDTO toDTO(Transacao transacao);
}


