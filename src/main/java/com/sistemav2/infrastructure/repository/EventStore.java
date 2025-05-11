package com.sistemav2.infrastructure.repository;

import com.sistemav2.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public class EventStore {
    private final List<DomainEvent> events = new ArrayList<>();

    public void save(DomainEvent event){
        events.add(event);
    }

    public List<DomainEvent> getEvents(){
        return events;
    }

}
