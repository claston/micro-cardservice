package com.sistemav2.application.command;

import java.util.UUID;

public class CriarCartaoCommand {
    private final UUID clienteId;

    public CriarCartaoCommand(UUID clienteId){
        this.clienteId = clienteId;
    }

    public UUID getClienteId(){
        return clienteId;
    }
}
