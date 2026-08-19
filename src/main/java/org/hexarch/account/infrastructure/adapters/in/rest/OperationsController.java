package org.hexarch.account.infrastructure.adapters.in.rest;

import org.hexarch.account.application.port.in.AccountOperationUseCase;
import org.hexarch.account.infrastructure.adapters.in.rest.dto.AccountDto;
import org.hexarch.account.infrastructure.adapters.in.rest.dto.CreateAccountDto;
import org.hexarch.account.infrastructure.adapters.in.rest.dto.TransferDto;
import org.hexarch.shared.infrastructure.rest.ApiWraped;
import org.jboss.resteasy.reactive.ResponseStatus;

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
    @ResponseStatus(200)
    @ApiWraped(message = "account.transfer.success")
    public void transfer(@Valid @NotNull(message = "Request body must not be empty") TransferDto transferRequest) {
        accountOperationUseCase.transfer(transferRequest.fromAccountId(), transferRequest.toAccountId(),
                transferRequest.amount());
    }

    @POST
    @Path("/create-account")
    @ResponseStatus(201)
    @ApiWraped(message = "account.created")
    public AccountDto createAccount(
            @Valid @NotNull(message = "Request body must not be empty") CreateAccountDto createAccountRequest) {
        return AccountDto.from(accountOperationUseCase.createAccount(createAccountRequest.holderName(),
                createAccountRequest.initialBalance(), createAccountRequest.email()));
    }
}
