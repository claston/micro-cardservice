package com.sistemav2.domain.event;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent {
    private final UUID id = UUID.randomUUID();
    private final Instant timestamp = Instant.now();

    private UUID getId(){
        return id;
    }

    private Instant getTimestamp(){
        return timestamp;
    }
}
