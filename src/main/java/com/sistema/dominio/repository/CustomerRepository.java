package com.sistema.dominio.repository;

import com.sistema.dominio.entidade.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    List<Customer> findAllAsList();
    Customer findById(UUID id);
}
