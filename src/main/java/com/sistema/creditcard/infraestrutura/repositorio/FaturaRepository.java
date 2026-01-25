package com.sistema.creditcard.infraestrutura.repositorio;

import com.sistema.creditcard.infraestrutura.entidade.FaturaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class FaturaRepository implements PanacheRepository<FaturaEntity> {

    public Optional<FaturaEntity> findByMesAno(LocalDate mesAno){
        return find("mesAno", mesAno).firstResultOptional();


    }
}


