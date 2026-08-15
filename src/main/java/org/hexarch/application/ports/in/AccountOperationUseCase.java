package org.hexarch.application.ports.in;

import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

import org.hexarch.domain.model.Account;

public interface AccountOperationUseCase {

    public Account createAccount(String holderName, BigDecimal initialBalance);

    public Optional<Account> getAccount(UUID accountId);

    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount);
    
}
