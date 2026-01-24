package com.sistema.ledger.domain.repository;

import com.sistema.ledger.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(UUID id);
}
