package com.sistema.customer.infraestrutura.repositorio;

import com.sistema.customer.dominio.entidade.Customer;
import com.sistema.customer.dominio.repository.CustomerRepository;
import com.sistema.customer.infraestrutura.entidade.CustomerEntity;
import com.sistema.infraestrutura.repositorio.DbCleanIT;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
class CustomerRepositoryAdapterTest extends DbCleanIT {

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CustomerRepositoryAdapter customerRepositoryAdapter;

    @Test
    @Transactional
    @Tag("integration-test")
    public void findAllAsListOrdenaPorNomeEValidaQuery() {

        Customer maria = new Customer("11111111111", "Maria Souza", "maria@email.com");
        Customer ana = new Customer("22222222222", "Ana Silva", "ana@email.com");

        customerRepositoryAdapter.save(maria);
        customerRepositoryAdapter.save(ana);

        List<Customer> customers = customerRepositoryAdapter.findAllAsList();

        //assertEquals(2, customers.size());
        assertEquals("Ana Silva", customers.get(0).getName());
        assertEquals("Maria Souza", customers.get(1).getName());
    }
}


