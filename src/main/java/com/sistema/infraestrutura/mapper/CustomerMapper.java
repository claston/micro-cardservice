package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.CustomerDTO;
import com.sistema.dominio.entidade.Customer;
import com.sistema.infraestrutura.entidade.CustomerEntity;
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
            @Mapping(target = "foneNumber", source = "foneNumber"),
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
            @Mapping(target = "foneNumber", source = "foneNumber"),
            @Mapping(target = "dataCadastro",  source = "dataCadastro")
    })
    Customer toDomain(CustomerEntity customerEntity);

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "foneNumber", source = "foneNumber")
    })
    Customer toDomain(CustomerDTO customerDTO);
}