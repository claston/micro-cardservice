package com.sistema.customer.infra.repository;

import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerStatus;
import com.sistema.customer.domain.model.CustomerType;
import com.sistema.customer.domain.repository.CustomerRepository;
import com.sistema.customer.infra.entity.CustomerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomerRepositoryAdapter implements CustomerRepository, PanacheRepository<CustomerEntity> {

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        persist(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Customer> findById(UUID tenantId, UUID id) {
        CustomerEntity entity = find("tenantId = ?1 and id = ?2", tenantId, id).firstResult();
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public Optional<Customer> findByDocument(UUID tenantId, CustomerDocumentType documentType, String documentNumber) {
        CustomerEntity entity = find("tenantId = ?1 and documentType = ?2 and documentNumber = ?3",
                tenantId,
                documentType.name(),
                documentNumber
        ).firstResult();
        return Optional.ofNullable(toDomain(entity));
    }

    private CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId());
        entity.setTenantId(customer.getTenantId());
        entity.setType(customer.getType().name());
        entity.setName(customer.getName());
        entity.setDocumentType(customer.getDocumentType().name());
        entity.setDocumentNumber(customer.getDocumentNumber());
        entity.setStatus(customer.getStatus().name());
        entity.setCreatedAt(customer.getCreatedAt());
        entity.setUpdatedAt(customer.getUpdatedAt());
        return entity;
    }

    private Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setId(entity.getId());
        customer.setTenantId(entity.getTenantId());
        customer.setType(CustomerType.valueOf(entity.getType()));
        customer.setName(entity.getName());
        customer.setDocumentType(CustomerDocumentType.valueOf(entity.getDocumentType()));
        customer.setDocumentNumber(entity.getDocumentNumber());
        customer.setStatus(CustomerStatus.valueOf(entity.getStatus()));
        customer.setCreatedAt(entity.getCreatedAt());
        customer.setUpdatedAt(entity.getUpdatedAt());
        return customer;
    }
}


