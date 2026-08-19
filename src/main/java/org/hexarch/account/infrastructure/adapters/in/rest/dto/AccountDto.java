package org.hexarch.account.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

import org.hexarch.account.domain.model.Account;

public record AccountDto(UUID id, String holderName, BigDecimal balance, String email) {

    public static AccountDto from(Account account) {
        return new AccountDto(account.id(), account.holderName(), account.balance(), account.email());
    }
}
