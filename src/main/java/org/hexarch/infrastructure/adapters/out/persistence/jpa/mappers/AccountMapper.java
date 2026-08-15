package org.hexarch.infrastructure.adapters.out.persistence.jpa.mappers;

import org.hexarch.domain.model.Account;
import org.hexarch.infrastructure.adapters.out.persistence.jpa.entities.AccountEntity;

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
