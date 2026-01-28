package com.sistema.customer.infra.repository;

import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerStatus;
import com.sistema.customer.domain.model.CustomerType;
import com.sistema.infraestrutura.repositorio.DbCleanIT;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class CustomerRepositoryAdapterTest extends DbCleanIT {
    @Inject
    CustomerRepositoryAdapter repository;

    @Test
    @Transactional
    void findByDocumentFiltersByTenant() {
        UUID tenantA = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID tenantB = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Customer customerA = new Customer();
        customerA.setId(UUID.randomUUID());
        customerA.setTenantId(tenantA);
        customerA.setType(CustomerType.INDIVIDUAL);
        customerA.setName("Maria A");
        customerA.setDocumentType(CustomerDocumentType.CPF);
        customerA.setDocumentNumber("12345678901");
        customerA.setStatus(CustomerStatus.ACTIVE);
        customerA.setCreatedAt(Instant.now());
        customerA.setUpdatedAt(Instant.now());
        repository.save(customerA);

        assertTrue(repository.findByDocument(tenantA, CustomerDocumentType.CPF, "12345678901").isPresent());
        assertTrue(repository.findByDocument(tenantB, CustomerDocumentType.CPF, "12345678901").isEmpty());
    }
}

