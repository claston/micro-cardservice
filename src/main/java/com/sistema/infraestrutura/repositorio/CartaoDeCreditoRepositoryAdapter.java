package com.sistema.infraestrutura.repositorio;

import com.sistema.dominio.entidade.CreditCard;
import com.sistema.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.infraestrutura.mapper.CartaoDeCreditoMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import com.sistema.dominio.repository.CartaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class CartaoDeCreditoRepositoryAdapter implements CartaoRepository, PanacheRepository<CartaoDeCreditoEntity> {

    @Inject
    CartaoDeCreditoMapper cartaoMapper;

    public CreditCard save(CreditCard cartao) {
        CartaoDeCreditoEntity entity = cartaoMapper.toEntity(cartao);
        persist(entity);
        getEntityManager().flush();
        return cartaoMapper.toDomain(entity);
    }

    public CreditCard findById(UUID id) {
        CartaoDeCreditoEntity entity = find("id", id).firstResult();
        return cartaoMapper.toDomain(entity);
    }
}
