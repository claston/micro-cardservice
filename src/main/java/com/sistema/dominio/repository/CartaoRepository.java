package com.sistema.dominio.repository;

import com.sistema.dominio.entidade.CartaoDeCredito;
import java.util.UUID;

public interface CartaoRepository {

    CartaoDeCredito save(CartaoDeCredito cartaoDeCredito);
    CartaoDeCredito findById(UUID id);
}
