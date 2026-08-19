package org.hexarch.account.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.account.domain.model.Account;

public interface AccountOperationUseCase {

    Account createAccount(String holderName, BigDecimal initialBalance, String email);

    Optional<Account> getAccount(UUID accountId);

    void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount);

    List<Account> listAccounts();
}
