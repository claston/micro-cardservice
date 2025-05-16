package com.sistema.infraestrutura.repositorio;

import com.sistema.infraestrutura.entidade.CustomerEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<CustomerEntity> {

    public CustomerEntity findById(UUID id) {
        return find("id", id).firstResult();
    }

    @Override
    public PanacheQuery<CustomerEntity> findAll() {
        return find("ORDER BY nome ASC");
    }

    public List<CustomerEntity> findAllAsList() {
        return list("ORDER BY nome ASC");
    }
}
