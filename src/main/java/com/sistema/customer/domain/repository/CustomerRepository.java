package com.sistema.customer.domain.repository;

import com.sistema.customer.domain.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    List<Customer> findAllAsList();
    Customer findById(UUID id);
}


