package com.sistema.creditcard.infraestrutura.mapper;

import com.sistema.creditcard.adaptadores.dto.CreditCardDTO;
import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.infraestrutura.entidade.CartaoDeCreditoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface CartaoDeCreditoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "cliente.id", source = "cliente.id")
    CartaoDeCreditoEntity toEntity(CreditCard cartao);

    @Mapping(target= "id", source ="id")
    @Mapping(target = "cliente.id", source = "cliente.id")
    CreditCard toDomain(CartaoDeCreditoEntity cartaoEntity);

    @Mapping(target= "id", source ="id")
    @Mapping(target = "cliente.id", source = "clienteId")
    CreditCard toDomain(CreditCardDTO creditCardDTO);

}


