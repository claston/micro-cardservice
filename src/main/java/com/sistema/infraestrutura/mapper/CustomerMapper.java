package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.ClienteDTO;
import com.sistema.dominio.entidade.Customer;
import com.sistema.infraestrutura.entidade.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "cdi")
public interface CustomerMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "nome", source = "nome"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "ativo", source = "ativo"),
            @Mapping(target = "cnpj", source = "cnpj"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "telefone", source = "telefone"),
            @Mapping(target = "dataCadastro",  source = "dataCadastro")

    })
    CustomerEntity toEntity(Customer customer);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "nome", source = "nome"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "ativo", source = "ativo"),
            @Mapping(target = "cnpj", source = "cnpj"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "telefone", source = "telefone"),
            @Mapping(target = "dataCadastro",  source = "dataCadastro")
    })
    Customer toDomain(CustomerEntity customerEntity);

    @Mappings({
            @Mapping(target = "nome", source = "nome"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "telefone", source = "telefone")
    })
    Customer toDomain(ClienteDTO clienteDTO);
}