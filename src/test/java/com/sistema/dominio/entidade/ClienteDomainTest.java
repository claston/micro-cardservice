package com.sistema.dominio.entidade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDomainTest {

    @Test
    public void testCriarClienteComSucesso(){

        Cliente cliente = new Cliente("1234567890", "José da Silva", "joao@email.com");

        assertNotNull(cliente);
        assertEquals("1234567890", cliente.getCpf());
        assertEquals("José da Silva", cliente.getNome());
        assertEquals("joao@email.com", cliente.getEmail());
        assertNotNull(cliente.getDataCadastro());
        assertTrue(cliente.isAtivo());
    }


    // Poderia criar verificar um cpf válido
    // Poderia Listar os cartões desse cliente
}