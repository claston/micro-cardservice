package com.sistemav2.application.handler;

import com.sistema.dominio.entidade.CreditCard;
import com.sistemav2.application.command.CriarCartaoCommand;
import com.sistemav2.domain.model.CartaoCriadoEvent;
import com.sistemav2.infrastructure.repository.EventStore;
import com.sistemav2.infrastructure.messaging.EventPublisher;

public class CriaCartaoHandler {
    private final EventStore eventStore;
    private final EventPublisher eventPublisher;

    public CriaCartaoHandler (EventStore eventStore, EventPublisher eventPublisher){
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    public void handle(CriarCartaoCommand command){
        // Criar novo cartão com UUID
        // TODO : Verificar essa questão de persistência - objetos com UUID já estão persistidos na base
        CreditCard cartao = new CreditCard();
        CartaoCriadoEvent event = new CartaoCriadoEvent(cartao);

        // Persisitr e Publicar o Evento
        eventStore.save(event);
        eventPublisher.publish(event);
    }
}
