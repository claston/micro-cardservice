package com.sistemav2.infrastructure.messaging;

import com.sistemav2.domain.event.DomainEvent;

public class EventPublisher {
    public void publish(DomainEvent event){
        System.out.println("Evento Publicado:" + event.getClass().getSimpleName());
        //KAFKA/ RABBIT
    }
}
