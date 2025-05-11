package com.sistemav2.domain.model;

import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistemav2.domain.event.DomainEvent;

import java.util.UUID;

public class CartaoCriadoEvent extends DomainEvent {

    private final UUID cartaoId;
    private final UUID clienteId;

    public CartaoCriadoEvent(CartaoDeCredito cartao) {
        this.cartaoId = cartao.getId();
        this.clienteId = cartao.getCliente().getId();
    }

    public UUID getCartaoId(){
        return cartaoId;
    }

    public UUID getClienteId(){
        return clienteId;
    }
}
