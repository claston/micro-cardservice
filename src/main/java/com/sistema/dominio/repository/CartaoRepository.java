package com.sistema.dominio.repository;

import com.sistema.dominio.entidade.CartaoDeCredito;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;

import java.util.UUID;

public interface CartaoRepository {

    CartaoDeCredito save(CartaoDeCredito cartaoDeCredito);
    CartaoDeCredito findById(UUID id);
}
