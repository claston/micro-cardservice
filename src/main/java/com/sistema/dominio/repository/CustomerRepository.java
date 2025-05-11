package com.sistema.dominio.repository;

import com.sistema.dominio.entidade.Cliente;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository {
    Cliente save(Cliente cliente);
    List<Cliente> findAllAsList();
    Cliente findById(UUID id);
}
