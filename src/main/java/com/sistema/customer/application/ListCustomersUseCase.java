package com.sistema.customer.application;

import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListCustomersUseCase {

    private final CustomerRepository customerRepository;

    public ListCustomersUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> execute() {
        return customerRepository.findAllAsList();
    }
}
