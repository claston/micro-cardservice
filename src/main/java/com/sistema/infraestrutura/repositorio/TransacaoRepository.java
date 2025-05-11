package com.sistema.infraestrutura.repositorio;

import com.sistema.infraestrutura.entidade.TransacaoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class TransacaoRepository  implements PanacheRepository<TransacaoEntity> {

    public TransacaoEntity findById(UUID id) {
        return find("id", id).firstResult();
    }
}
