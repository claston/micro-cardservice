package com.sistema.casodeuso;

import com.sistema.dominio.entidade.Cliente;
import com.sistema.infraestrutura.entidade.ClienteEntity;
import com.sistema.dominio.repository.CustomerRepository;
import com.sistema.infraestrutura.mapper.ClienteMapper;
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
    public Cliente executar (Cliente cliente){
        return customerRepository.save(cliente);
    }

    public Cliente findById (UUID id){
        return customerRepository.findById(id);
    }
}
