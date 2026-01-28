package com.sistema.customer.application;

import com.sistema.customer.application.exception.CustomerNotFoundException;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class GetCustomerUseCase {
    private final CustomerRepository customerRepository;

    public GetCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(UUID tenantId, UUID customerId) {
        return customerRepository.findById(tenantId, customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}

