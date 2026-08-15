package org.hexarch.application.ports.out;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.domain.model.Account;

public interface AccountRepositoryPort {

    public Account save(Account account);

    public Optional<Account> findById(UUID accountId);

    public Account findByHolderName(String holderName);
    
}