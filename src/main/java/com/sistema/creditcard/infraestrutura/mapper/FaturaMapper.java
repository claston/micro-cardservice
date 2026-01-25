package com.sistema.creditcard.infraestrutura.mapper;

import com.sistema.creditcard.dominio.entidade.Fatura;
import com.sistema.creditcard.infraestrutura.entidade.FaturaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface FaturaMapper {

    @Mapping(target="id", source="id")
    FaturaEntity toEntity(Fatura fatura);

    @Mapping(target="id", source="id")
    Fatura toDomain(FaturaEntity faturaEntity);
}


