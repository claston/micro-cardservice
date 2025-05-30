package com.sistema.infraestrutura.repositorio;

import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.repository.CustomerRepository;
import com.sistema.infraestrutura.entidade.CustomerEntity;
import com.sistema.infraestrutura.mapper.CustomerMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerRepositoryAdapter implements CustomerRepository, PanacheRepository<CustomerEntity> {

    @Inject
    CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = customerMapper.toEntity(customer);
        persist(entity);
        return customerMapper.toDomain(entity);
    }

    @Override
    public List<Customer> findAllAsList() {
        List<CustomerEntity> entities = list("ORDER BY nome ASC");
        return entities.stream()
                .map(customerMapper::toDomain)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Customer findById(UUID id) {
        CustomerEntity entity = find("id", id).firstResult();
        return customerMapper.toDomain(entity);
    }
}
