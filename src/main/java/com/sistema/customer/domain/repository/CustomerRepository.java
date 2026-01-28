package com.sistema.customer.domain.repository;

import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer> findById(UUID tenantId, UUID id);

    Optional<Customer> findByDocument(UUID tenantId, CustomerDocumentType documentType, String documentNumber);
}


