package com.sistema.infraestrutura.repositorio;

import com.sistema.dominio.entidade.Cliente;
import com.sistema.dominio.repository.CustomerRepository;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.infraestrutura.mapper.ClienteMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerRepositoryAdapter implements CustomerRepository, PanacheRepository<ClienteEntity> {

    @Inject
    ClienteMapper clienteMapper;

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = clienteMapper.toEntity(cliente);
        persist(entity);
        return clienteMapper.toDomain(entity);
    }

    @Override
    public List<Cliente> findAllAsList() {
        List<ClienteEntity> entities = list("ORDER BY nome ASC");
        return entities.stream()
                .map(clienteMapper::toDomain)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Cliente findById(UUID id) {
        ClienteEntity entity = find("id", id).firstResult();
        return clienteMapper.toDomain(entity);
    }
}
