package com.sistema.infraestrutura.repositorio;

import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CartaoDeCreditoRepository implements PanacheRepository<CartaoDeCreditoEntity>{

    public CartaoDeCreditoEntity findById(UUID id) {
        return find("id", id).firstResult();
    }
}
