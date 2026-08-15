package org.hexarch.infrastructure.adapters.out.persistence.jpa.repositories;

import java.util.Optional;
import java.util.UUID;
import org.hexarch.application.ports.out.AccountRepositoryPort;
import org.hexarch.domain.model.Account;
import org.hexarch.infrastructure.adapters.out.persistence.jpa.entities.AccountEntity;
import org.hexarch.infrastructure.adapters.out.persistence.jpa.mappers.AccountMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);

        entity.persist();
        return AccountMapper.toDomain(entity);
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        AccountEntity entity = AccountEntity.findById(accountId);
        return entity != null ? Optional.of(AccountMapper.toDomain(entity)) : Optional.empty();
    }

    @Override
    public Account findByHolderName(String holderName) {
        AccountEntity entity = AccountEntity.find("holderName", holderName).firstResult();
        return entity != null ? AccountMapper.toDomain(entity) : null;
    }

}
