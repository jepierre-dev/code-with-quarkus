package org.hexarch.account.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.account.domain.model.Account;

public interface AccountRepositoryPort {

    Account create(Account account);

    Account update(Account account);

    Optional<Account> findById(UUID accountId);

    boolean existsByHolderName(String holderName);

    boolean existsByEmail(String email);

    List<Account> listAccounts();
}
