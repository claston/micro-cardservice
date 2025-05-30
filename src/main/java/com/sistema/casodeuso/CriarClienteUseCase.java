package com.sistema.casodeuso;

import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class CriarClienteUseCase {

    private final CustomerRepository customerRepository;

    public CriarClienteUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer executar (Customer customer){
        return customerRepository.save(customer);
    }

    public Customer findById (UUID id){
        return customerRepository.findById(id);
    }
}
