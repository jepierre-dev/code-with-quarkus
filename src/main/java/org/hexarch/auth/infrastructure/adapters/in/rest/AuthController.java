package org.hexarch.auth.infrastructure.adapters.in.rest;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hexarch.auth.application.port.in.AuthUseCase;
import org.hexarch.auth.infrastructure.adapters.in.rest.dto.AuthTokenDto;
import org.hexarch.auth.infrastructure.adapters.in.rest.dto.ChangePasswordDto;
import org.hexarch.auth.infrastructure.adapters.in.rest.dto.LoginDto;
import org.hexarch.auth.infrastructure.adapters.in.rest.dto.RegisterDto;
import org.hexarch.shared.infrastructure.rest.ApiWraped;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkus.security.Authenticated;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    private final AuthUseCase authUseCase;

    @Inject
    JsonWebToken jwt;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @POST
    @Path("/register")
    @PermitAll
    @ApiWraped(message = "auth.register.success")
    public AuthTokenDto register(@Valid RegisterDto request) {
        return AuthTokenDto.from(authUseCase.register(request.username(), request.email(), request.password()));
    }

    @POST
    @Path("/login")
    @PermitAll
    @ApiWraped(message = "auth.login.success")
    public AuthTokenDto login(@Valid LoginDto request) {
        return AuthTokenDto.from(authUseCase.login(request.email(), request.password()));
    }

    @PATCH
    @Path("/password")
    @Authenticated
    public void changePassword(@Valid ChangePasswordDto request) {
        authUseCase.changePassword(UUID.fromString(jwt.getSubject()), request.currentPassword(),
                request.newPassword());
    }
}
