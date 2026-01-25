package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.infraestrutura.entidade.CartaoDeCreditoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CartaoDeCreditoRepository implements PanacheRepository<CartaoDeCreditoEntity>{

    public CartaoDeCreditoEntity findById(UUID id) {
        return find("id", id).firstResult();
    }
}


