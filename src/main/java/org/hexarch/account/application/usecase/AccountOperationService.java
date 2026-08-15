package org.hexarch.account.application.usecase;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.account.application.port.in.AccountOperationUseCase;
import org.hexarch.account.application.port.out.AccountRepositoryPort;
import org.hexarch.account.domain.Account;
import org.hexarch.account.domain.AccountErrors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AccountOperationService implements AccountOperationUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    public AccountOperationService(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    @Transactional
    public Account createAccount(String holderName, BigDecimal initialBalance, String email) {
        Account account = new Account(null, holderName, initialBalance, email);

        if (accountRepositoryPort.existsByHolderName(holderName)) {
            throw AccountErrors.holderNameAlreadyExists(holderName);
        }

        if (email != null && accountRepositoryPort.existsByEmail(email)) {
            throw AccountErrors.emailAlreadyExists(email);
        }

        return accountRepositoryPort.create(account);
    }

    @Override
    public Optional<Account> getAccount(UUID accountId) {
        return accountRepositoryPort.findById(accountId);
    }

    @Override
    @Transactional
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        Account fromAccount = accountRepositoryPort.findById(fromAccountId)
                .orElseThrow(() -> AccountErrors.accountNotFound(fromAccountId));
        Account toAccount = accountRepositoryPort.findById(toAccountId)
                .orElseThrow(() -> AccountErrors.accountNotFound(toAccountId));

        Account updatedFromAccount = fromAccount.withDraw(amount);
        Account updatedToAccount = toAccount.deposit(amount);

        accountRepositoryPort.update(updatedFromAccount);
        accountRepositoryPort.update(updatedToAccount);
    }
}
