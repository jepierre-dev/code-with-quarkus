package org.hexarch.infrastructure.adapters.out.persistence.jpa.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.hexarch.application.ports.out.AccountRepositoryPort;
import org.hexarch.domain.exceptions.ErrorCode;
import org.hexarch.domain.exceptions.DomainException;
import org.hexarch.domain.model.Account;
import org.hexarch.infrastructure.adapters.out.persistence.jpa.entities.AccountEntity;
import org.hexarch.infrastructure.adapters.out.persistence.jpa.mappers.AccountMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    @Override
    public Account create(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);

        entity.persist();
        return AccountMapper.toDomain(entity);
    }

    @Override
    public Account update(Account account) {
        AccountEntity managed = AccountEntity.findById(account.id());
        if (managed == null) {
            throw new DomainException.NotFound(ErrorCode.ACCOUNT_NOT_FOUND, "Account not found",
                    Map.of("accountId", account.id()));
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
