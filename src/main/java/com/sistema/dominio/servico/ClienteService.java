package com.sistema.dominio.servico;

import com.sistema.dominio.entidade.Cliente;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.mapper.ClienteMapper;
import com.sistema.infraestrutura.repositorio.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional
    public void salvarCliente(Cliente cliente){

        ClienteEntity entity = clienteMapper.toEntity(cliente);
        clienteRepository.persist(entity);
    }

    public Cliente buscarCliente (UUID id) {
        ClienteEntity entity = clienteRepository.findById(id);
        return clienteMapper.toDomain(entity);

    }

}
