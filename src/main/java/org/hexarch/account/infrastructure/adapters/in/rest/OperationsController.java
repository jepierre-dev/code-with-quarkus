package org.hexarch.account.infrastructure.adapters.in.rest;

import java.util.UUID;

import org.hexarch.account.application.port.in.AccountOperationUseCase;
import org.hexarch.account.infrastructure.adapters.in.rest.dto.CreateAccountDto;
import org.hexarch.account.infrastructure.adapters.in.rest.dto.TransferDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/operations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OperationsController {

    private final AccountOperationUseCase accountOperationUseCase;

    public OperationsController(AccountOperationUseCase accountOperationUseCase) {
        this.accountOperationUseCase = accountOperationUseCase;
    }

    @POST
    @Path("/transfer")
    public String transfer(@Valid @NotNull(message = "Request body must not be empty") TransferDto transferRequest) {
        UUID fromAccountId = transferRequest.fromAccountId();
        UUID toAccountId = transferRequest.toAccountId();
        accountOperationUseCase.transfer(fromAccountId, toAccountId, transferRequest.amount());
        return "Transfer operation executed successfully.";
    }

    @POST
    @Path("/create-account")
    public String createAccount(
            @Valid @NotNull(message = "Request body must not be empty") CreateAccountDto createAccountRequest) {
        accountOperationUseCase.createAccount(createAccountRequest.holderName(),
                createAccountRequest.initialBalance(), createAccountRequest.email());
        return "Account created successfully.";
    }
}
