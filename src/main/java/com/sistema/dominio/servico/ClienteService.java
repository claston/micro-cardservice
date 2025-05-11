package com.sistema.dominio.servico;

import com.sistema.dominio.entidade.Cliente;
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
    public void salvarCliente(Cliente cliente){
        customerRepository.save(cliente);
    }

    public Cliente buscarCliente (UUID id) {
        return customerRepository.findById(id);

    }

}
