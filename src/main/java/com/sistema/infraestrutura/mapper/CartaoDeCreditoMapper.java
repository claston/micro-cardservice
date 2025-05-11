package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.CartaoDeCreditoDTO;
import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface CartaoDeCreditoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cliente.id", source = "cliente.id")
    CartaoDeCreditoEntity toEntity(CartaoDeCredito cartao);

    @Mapping(target= "id", source ="id")
    @Mapping(target = "cliente.id", source = "cliente.id")
    CartaoDeCredito toDomain(CartaoDeCreditoEntity cartaoEntity);

    @Mapping(target= "id", source ="id")
    @Mapping(target = "cliente.id", source = "clienteId")
    CartaoDeCredito toDomain(CartaoDeCreditoDTO cartaoDeCreditoDTO);

}
