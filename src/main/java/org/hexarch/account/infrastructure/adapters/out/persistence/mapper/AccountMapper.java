package org.hexarch.account.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.account.domain.model.Account;
import org.hexarch.account.infrastructure.adapters.out.persistence.entity.AccountEntity;

public class AccountMapper {

    public static AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.id = account.id();
        entity.holderName = account.holderName();
        entity.balance = account.balance();
        entity.email = account.email();
        return entity;
    }

    public static Account toDomain(AccountEntity entity) {
        return new Account(entity.id, entity.holderName, entity.balance, entity.email);
    }
}
