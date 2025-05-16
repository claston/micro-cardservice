package com.sistema.dominio.servico;

import com.sistema.dominio.entidade.Customer;
import com.sistema.dominio.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class ClienteService {

    private final CustomerRepository customerRepository;

    public ClienteService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void salvarCliente(Customer customer){
        customerRepository.save(customer);
    }

    public Customer buscarCliente (UUID id) {
        return customerRepository.findById(id);

    }

}
