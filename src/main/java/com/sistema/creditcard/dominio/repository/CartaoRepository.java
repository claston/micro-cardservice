package com.sistema.creditcard.dominio.repository;

import com.sistema.creditcard.dominio.entidade.CreditCard;

import java.util.UUID;

public interface CartaoRepository {

    CreditCard save(CreditCard creditCard);
    CreditCard findById(UUID id);
}


