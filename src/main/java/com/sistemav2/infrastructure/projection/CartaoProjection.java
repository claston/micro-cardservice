package com.sistemav2.infrastructure.projection;

import com.sistemav2.domain.model.CartaoCriadoEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CartaoProjection {

    private static final ConcurrentHashMap<UUID,UUID> cartaoPorCliente = new ConcurrentHashMap<>();

    public void onCartaoCriado(CartaoCriadoEvent event){
        cartaoPorCliente.put(event.getCartaoId(), event.getClienteId());
    }

    public UUID getClientePorCartao(String cartaoId) {
        return cartaoPorCliente.get(cartaoId);
    }
}
