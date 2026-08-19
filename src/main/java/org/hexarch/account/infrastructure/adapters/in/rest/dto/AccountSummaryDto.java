package org.hexarch.account.infrastructure.adapters.in.rest.dto;

import java.util.UUID;

import org.hexarch.account.domain.model.Account;

public record AccountSummaryDto(
  UUID id,
  String holderName,
  String email
) {
  public static AccountSummaryDto from(Account account) {
    return new AccountSummaryDto(account.id(), account.holderName(), account.email());
  }
}
