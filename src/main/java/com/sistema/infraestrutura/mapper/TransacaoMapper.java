package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.TransacaoDTO;
import com.sistema.dominio.entidade.Transacao;
import com.sistema.infraestrutura.entidade.TransacaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface TransacaoMapper {

    @Mapping(target = "cartao.id", source = "cartao.id")
    @Mapping(target = "fatura", ignore = true)
    TransacaoEntity toEntity(Transacao transacao);

    @Mapping(target = "cartao.id", source = "cartao.id")
    @Mapping(target = "fatura", ignore = true)
    Transacao toDomain(TransacaoEntity transacaoEntityEntity);

    @Mapping(target = "cartao.id", source = "cartaoId")
    @Mapping(target = "fatura", ignore = true)
    Transacao toDomain(TransacaoDTO transacaoDTO);

    @Mapping(target = "cartaoId", source = "cartao.id")
    TransacaoDTO toDTO(Transacao transacao);
}
