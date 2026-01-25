package com.sistema.creditcard.dominio.repository;

import com.sistema.creditcard.dominio.entidade.Fatura;
import java.time.LocalDate;
import java.util.Optional;

public interface FaturaRepository
{
    Optional<Fatura>findByMesAno(LocalDate mesAno);
    Fatura save(Fatura fatura);
}


