package com.sistema.casodeuso;

import com.sistema.dominio.entidade.Cliente;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.repositorio.ClienteRepository;
import com.sistema.infraestrutura.mapper.ClienteMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class CriarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public CriarClienteUseCase(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional
    public Cliente executar (Cliente cliente){
        var clienteEntity = clienteMapper.toEntity(cliente);
        clienteRepository.persist(clienteEntity);
        return clienteMapper.toDomain(clienteEntity);
    }

    public ClienteEntity findById (UUID id){
        return clienteRepository.findById(id);
    }
}
