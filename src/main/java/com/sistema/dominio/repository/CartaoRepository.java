package com.sistema.dominio.repository;

import com.sistema.dominio.entidade.CreditCard;

import java.util.UUID;

public interface CartaoRepository {

    CreditCard save(CreditCard creditCard);
    CreditCard findById(UUID id);
}
