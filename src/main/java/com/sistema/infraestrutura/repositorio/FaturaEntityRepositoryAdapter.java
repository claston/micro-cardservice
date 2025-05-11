package com.sistema.infraestrutura.repositorio;

import com.sistema.dominio.entidade.Fatura;
import com.sistema.dominio.repository.FaturaRepository;
import com.sistema.infraestrutura.entidade.FaturaEntity;
import com.sistema.infraestrutura.mapper.FaturaMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class FaturaEntityRepositoryAdapter implements FaturaRepository, PanacheRepository<FaturaEntity> {

    @Inject
    FaturaMapper faturaMapper;

    @Override
    public Optional<Fatura> findByMesAno(LocalDate mesAno) {
        Optional<FaturaEntity> entityOpt = find("mesAno", mesAno).firstResultOptional();
        return entityOpt.map(faturaMapper::toDomain);
    }

    @Override
    public Fatura save(Fatura fatura) {
        FaturaEntity entity = faturaMapper.toEntity(fatura);
        persist(entity);
        return faturaMapper.toDomain(entity);
    }
}
