package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.ClienteDTO;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "cdi")
public interface ClienteMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "nome", source = "nome"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "ativo", source = "ativo"),
            @Mapping(target = "cnpj", source = "cnpj"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "telefone", source = "telefone"),
            @Mapping(target = "dataCadastro",  source = "dataCadastro")

    })    ClienteEntity toEntity(Cliente cliente);

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
    Cliente toDomain(ClienteEntity clienteEntity);

    @Mappings({
            @Mapping(target = "nome", source = "nome"),
            @Mapping(target = "cpf", source = "cpf"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "telefone", source = "telefone")
    })
    Cliente toDomain(ClienteDTO clienteDTO);
}