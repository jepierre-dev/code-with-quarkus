package org.hexarch.application.usecases;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.application.ports.in.AccountOperationUseCase;
import org.hexarch.application.ports.out.AccountRepositoryPort;
import org.hexarch.domain.exceptions.DuplicateResourceException;
import org.hexarch.domain.exceptions.ErrorCode;
import org.hexarch.domain.exceptions.ResourceNotFoundException;
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
    public Account createAccount(String holderName, BigDecimal initialBalance, String email) {
        Account account = new Account(null, holderName, initialBalance, email);

        if (accountRepositoryPort.existsByHolderName(holderName)) {
            throw new DuplicateResourceException(ErrorCode.HOLDER_NAME_ALREADY_EXISTS,
                    "Account with holder name already exists", Map.of("holderName", holderName));
        }

        if(email != null && accountRepositoryPort.existsByEmail(email)) {
            throw new DuplicateResourceException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Account with email already exists", Map.of("email", email));
        }
        
        return accountRepositoryPort.create(account);
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

        Account fromAccount = accountRepositoryPort.findById(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND,
                        "From account not found", Map.of("accountId", fromAccountId)));
        Account toAccount = accountRepositoryPort.findById(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND,
                        "To account not found", Map.of("accountId", toAccountId)));
    
        // Calcular nuevos balances
        Account updatedFromAccount = fromAccount.withDraw(amount);
        Account updatedToAccount = toAccount.deposit(amount);


        // Actualizar cuentas en el repositorio
        accountRepositoryPort.update(updatedFromAccount);
        accountRepositoryPort.update(updatedToAccount);
    }
    
}
