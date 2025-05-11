package com.sistema.dominio.repository;

import com.sistema.dominio.entidade.Fatura;
import java.time.LocalDate;
import java.util.Optional;

public interface FaturaRepository
{
    Optional<Fatura>findByMesAno(LocalDate mesAno);
    Fatura save(Fatura fatura);
}
