package com.sistema.infraestrutura.mapper;

import com.sistema.adaptadores.dto.ClienteDTO;
import com.sistema.dominio.entidade.Cliente;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import jakarta.enterprise.context.ApplicationScoped;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-10T21:35:30-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (JetBrains s.r.o.)"
)
@ApplicationScoped
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteEntity toEntity(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteEntity clienteEntity = new ClienteEntity();

        clienteEntity.setId( cliente.getId() );
        clienteEntity.setNome( cliente.getNome() );
        clienteEntity.setCpf( cliente.getCpf() );
        clienteEntity.setAtivo( cliente.isAtivo() );
        clienteEntity.setCnpj( cliente.getCnpj() );
        clienteEntity.setEmail( cliente.getEmail() );
        clienteEntity.setTelefone( cliente.getTelefone() );
        clienteEntity.setDataCadastro( cliente.getDataCadastro() );

        return clienteEntity;
    }

    @Override
    public Cliente toDomain(ClienteEntity clienteEntity) {
        if ( clienteEntity == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setId( clienteEntity.getId() );
        cliente.setNome( clienteEntity.getNome() );
        cliente.setCpf( clienteEntity.getCpf() );
        cliente.setAtivo( clienteEntity.isAtivo() );
        cliente.setCnpj( clienteEntity.getCnpj() );
        cliente.setEmail( clienteEntity.getEmail() );
        cliente.setTelefone( clienteEntity.getTelefone() );
        cliente.setDataCadastro( clienteEntity.getDataCadastro() );

        return cliente;
    }

    @Override
    public Cliente toDomain(ClienteDTO clienteDTO) {
        if ( clienteDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setNome( clienteDTO.getNome() );
        cliente.setCpf( clienteDTO.getCpf() );
        cliente.setEmail( clienteDTO.getEmail() );
        cliente.setTelefone( clienteDTO.getTelefone() );

        return cliente;
    }
}
