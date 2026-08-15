package org.hexarch.application.ports.out;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.domain.model.Account;

public interface AccountRepositoryPort {

    public Account create(Account account);

    public Account update(Account account);

    public Optional<Account> findById(UUID accountId);

    public boolean existsByHolderName(String holderName);
    
    public boolean existsByEmail(String email);
}