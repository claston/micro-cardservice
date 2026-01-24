package com.sistema.dominio.entidade;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDomainTest {

    @Test
    public void testCriarClienteComSucesso(){

        Customer customer = new Customer("1234567890", "Jose da Silva", "joao@email.com");

        assertNotNull(customer);
        assertEquals("1234567890", customer.getCpf());
        assertEquals("Jose da Silva", customer.getName());
        assertEquals("joao@email.com", customer.getEmail());
        assertNotNull(customer.getDataCadastro());
        assertTrue(customer.isAtivo());
    }

    @Test
    public void testCreateValidCustomerNormalizaDados() {
        LocalDate registrationDate = LocalDate.of(2024, 1, 10);

        Customer customer = Customer.createValidCustomer("Jose da Silva", "JOAO@EMAIL.COM", registrationDate);

        assertNotNull(customer);
        assertEquals("12345678890", customer.getCpf());
        assertEquals("Jose da Silva", customer.getName());
        assertEquals("joao@email.com", customer.getEmail());
        assertTrue(customer.isAtivo());
        assertNotNull(customer.getDataCadastro());
    }

    @Test
    public void testCreateValidCustomerComNomeEmBrancoLancaErro() {
        LocalDate registrationDate = LocalDate.of(2024, 1, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Customer.createValidCustomer("   ", "joao@email.com", registrationDate));

        assertEquals("Name cannot be blank", exception.getMessage());
    }

    @Test
    public void testCreateValidCustomerComEmailInvalidoLancaErro() {
        LocalDate registrationDate = LocalDate.of(2024, 1, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Customer.createValidCustomer("Jose da Silva", "email-invalido", registrationDate));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testCreateValidCustomerComNulosLancaErro() {
        LocalDate registrationDate = LocalDate.of(2024, 1, 10);

        NullPointerException nameNull = assertThrows(NullPointerException.class,
                () -> Customer.createValidCustomer(null, "joao@email.com", registrationDate));
        assertEquals("Name cannot be null", nameNull.getMessage());

        NullPointerException emailNull = assertThrows(NullPointerException.class,
                () -> Customer.createValidCustomer("Jose da Silva", null, registrationDate));
        assertEquals("Email cannot be null", emailNull.getMessage());

        NullPointerException dateNull = assertThrows(NullPointerException.class,
                () -> Customer.createValidCustomer("Jose da Silva", "joao@email.com", null));
        assertEquals("Registration Date cannot be null", dateNull.getMessage());
    }

    // Poderia criar verificar um cpf valido
    // Poderia listar os cartoes desse cliente
}
