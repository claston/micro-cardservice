package com.sistema.dominio.entidade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDomainTest {

    @Test
    public void testCriarClienteComSucesso(){

        Customer customer = new Customer("1234567890", "José da Silva", "joao@email.com");

        assertNotNull(customer);
        assertEquals("1234567890", customer.getCpf());
        assertEquals("José da Silva", customer.getName());
        assertEquals("joao@email.com", customer.getEmail());
        assertNotNull(customer.getDataCadastro());
        assertTrue(customer.isAtivo());
    }


    // Poderia criar verificar um cpf válido
    // Poderia Listar os cartões desse cliente
}