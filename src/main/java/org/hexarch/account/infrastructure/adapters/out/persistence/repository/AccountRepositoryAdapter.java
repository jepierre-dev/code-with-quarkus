package org.hexarch.account.infrastructure.adapters.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.account.application.port.out.AccountRepositoryPort;
import org.hexarch.account.domain.exceptions.AccountErrors;
import org.hexarch.account.domain.model.Account;
import org.hexarch.account.infrastructure.adapters.out.persistence.entity.AccountEntity;
import org.hexarch.account.infrastructure.adapters.out.persistence.mapper.AccountMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    @Override
    public Account create(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);

        entity.persist();
        return AccountMapper.toDomain(entity);
    }

    // Muta la instancia gestionada: Hibernate emite el UPDATE por dirty checking al hacer flush.
    @Override
    public Account update(Account account) {
        AccountEntity managed = AccountEntity.findById(account.id());
        if (managed == null) {
            throw AccountErrors.accountNotFound(account.id());
        }

        managed.holderName = account.holderName();
        managed.balance = account.balance();
        managed.email = account.email();
        return AccountMapper.toDomain(managed);
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        AccountEntity entity = AccountEntity.findById(accountId);
        return entity != null ? Optional.of(AccountMapper.toDomain(entity)) : Optional.empty();
    }

    @Override
    public boolean existsByHolderName(String holderName) {
        return AccountEntity.count("holderName", holderName) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return AccountEntity.count("email", email) > 0;
    }
}
