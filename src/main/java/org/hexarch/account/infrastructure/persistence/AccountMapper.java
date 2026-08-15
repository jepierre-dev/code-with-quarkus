package org.hexarch.account.infrastructure.persistence;

import org.hexarch.account.domain.Account;

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
