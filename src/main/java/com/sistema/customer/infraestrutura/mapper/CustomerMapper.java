package com.sistema.customer.infraestrutura.mapper;

import com.sistema.customer.adaptadores.dto.CustomerDTO;
import com.sistema.customer.dominio.entidade.Customer;
import com.sistema.customer.infraestrutura.entidade.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "cdi")
public interface CustomerMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "ativo", source = "ativo"),
            @Mapping(target = "cnpj", source = "cnpj"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "dataCadastro",  source = "dataCadastro")

    })
    CustomerEntity toEntity(Customer customer);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "ativo", source = "ativo"),
            @Mapping(target = "cnpj", source = "cnpj"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "dataCadastro",  source = "dataCadastro")
    })
    Customer toDomain(CustomerEntity customerEntity);

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "phoneNumber", source = "phoneNumber")
    })
    Customer toDomain(CustomerDTO customerDTO);
}


