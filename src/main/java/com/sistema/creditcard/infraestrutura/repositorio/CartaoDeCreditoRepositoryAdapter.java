package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.dominio.entidade.CreditCard;
import com.sistema.creditcard.infraestrutura.entidade.CartaoDeCreditoEntity;
import com.sistema.creditcard.infraestrutura.mapper.CartaoDeCreditoMapper;
import com.sistema.customer.infra.entity.CustomerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import com.sistema.creditcard.dominio.repository.CartaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class CartaoDeCreditoRepositoryAdapter implements CartaoRepository, PanacheRepository<CartaoDeCreditoEntity> {

    @Inject
    CartaoDeCreditoMapper cartaoMapper;

    public CreditCard save(CreditCard cartao) {
        CartaoDeCreditoEntity entity = cartaoMapper.toEntity(cartao);
        if (entity.getCliente() != null && entity.getCliente().getId() != null) {
            entity.setCliente(getEntityManager().getReference(CustomerEntity.class, entity.getCliente().getId()));
        }
        persist(entity);
        getEntityManager().flush();
        return cartaoMapper.toDomain(entity);
    }

    public CreditCard findById(UUID id) {
        CartaoDeCreditoEntity entity = find("id", id).firstResult();
        return cartaoMapper.toDomain(entity);
    }
}


