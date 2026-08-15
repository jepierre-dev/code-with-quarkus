package org.hexarch.application.usecases;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.application.ports.in.AccountOperationUseCase;
import org.hexarch.application.ports.out.AccountRepositoryPort;
import org.hexarch.domain.exceptions.BusinessException;
import org.hexarch.domain.model.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class AccountOperationService implements AccountOperationUseCase {

    @Inject
    private AccountRepositoryPort accountRepositoryPort;



    @Override
    @Transactional
    public Account createAccount(String holderName, BigDecimal initialBalance) {
        Account account = new Account(null, holderName, initialBalance);
        Account existingAccount = accountRepositoryPort.findByHolderName(holderName);
        if (existingAccount != null) {
            throw new BusinessException("Account with holder name '" + holderName + "' already exists.");
        }
        return accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    public Optional<Account> getAccount(UUID accountId) {
        return accountRepositoryPort.findById(accountId);
    }

    @Override
    @Transactional
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {

        // Buscar cuentas de origen y destino

        Account fromAccount = accountRepositoryPort.findById(fromAccountId).orElseThrow(() -> new BusinessException("From account not found"));
        Account toAccount = accountRepositoryPort.findById(toAccountId).orElseThrow(() -> new BusinessException("To account not found"));
    
        // Calcular nuevos balances
        Account updatedFromAccount = fromAccount.withDraw(amount);
        Account updatedToAccount = toAccount.deposit(amount);


        // Actualizar cuentas en el repositorio
        accountRepositoryPort.save(updatedFromAccount);
        accountRepositoryPort.save(updatedToAccount);
    }
    
}
