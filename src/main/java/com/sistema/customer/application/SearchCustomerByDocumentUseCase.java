package com.sistema.customer.application;

import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SearchCustomerByDocumentUseCase {
    private final CustomerRepository customerRepository;

    public SearchCustomerByDocumentUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> execute(UUID tenantId, CustomerDocumentType documentType, String rawDocumentNumber) {
        String normalized = DocumentNumberNormalizer.normalize(rawDocumentNumber);
        if (normalized == null) {
            return Optional.empty();
        }
        return customerRepository.findByDocument(tenantId, documentType, normalized);
    }
}

