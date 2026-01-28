package com.sistema.customer.application;

import com.sistema.customer.application.command.CreateCustomerCommand;
import com.sistema.customer.application.exception.CustomerAlreadyExistsException;
import com.sistema.customer.domain.model.Customer;
import com.sistema.customer.domain.model.CustomerDocumentType;
import com.sistema.customer.domain.model.CustomerStatus;
import com.sistema.customer.domain.model.CustomerType;
import com.sistema.customer.domain.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class CreateCustomerUseCase {
    private final CustomerRepository customerRepository;

    public CreateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer execute(UUID tenantId, CreateCustomerCommand command) {
        CustomerType type = CustomerType.valueOf(command.getType());
        CustomerDocumentType documentType = CustomerDocumentType.valueOf(command.getDocumentType());
        String normalizedDocument = DocumentNumberNormalizer.normalize(command.getDocumentNumber());

        validateConsistency(type, documentType);
        validateDocument(documentType, normalizedDocument);

        if (customerRepository.findByDocument(tenantId, documentType, normalizedDocument).isPresent()) {
            throw new CustomerAlreadyExistsException();
        }

        Instant now = Instant.now();
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setTenantId(tenantId);
        customer.setType(type);
        customer.setName(command.getName() == null ? null : command.getName().trim());
        customer.setDocumentType(documentType);
        customer.setDocumentNumber(normalizedDocument);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);
        return customerRepository.save(customer);
    }

    private void validateConsistency(CustomerType type, CustomerDocumentType documentType) {
        if (type == CustomerType.INDIVIDUAL && documentType != CustomerDocumentType.CPF) {
            throw new IllegalArgumentException("INDIVIDUAL customer must use documentType=CPF");
        }
        if (type == CustomerType.BUSINESS && documentType != CustomerDocumentType.CNPJ) {
            throw new IllegalArgumentException("BUSINESS customer must use documentType=CNPJ");
        }
    }

    private void validateDocument(CustomerDocumentType documentType, String normalizedDocument) {
        if (normalizedDocument == null) {
            throw new IllegalArgumentException("documentNumber is required");
        }
        int length = normalizedDocument.length();
        if (documentType == CustomerDocumentType.CPF && length != 11) {
            throw new IllegalArgumentException("CPF documentNumber must have 11 digits");
        }
        if (documentType == CustomerDocumentType.CNPJ && length != 14) {
            throw new IllegalArgumentException("CNPJ documentNumber must have 14 digits");
        }
    }
}

