package com.sistema.infraestrutura.mapper;

import com.sistema.dominio.entidade.Fatura;
import com.sistema.infraestrutura.entidade.FaturaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface FaturaMapper {

    @Mapping(target="id", source="id")
    FaturaEntity toEntity(Fatura fatura);

    @Mapping(target="id", source="id")
    Fatura toDomain(FaturaEntity faturaEntity);
}
