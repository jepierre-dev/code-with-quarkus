package org.hexarch.user.infrastructure.adapters.in.rest;

import java.util.UUID;

import org.hexarch.shared.domain.security.PlatformPermission;
import org.hexarch.shared.infrastructure.rest.ApiWraped;
import org.hexarch.shared.infrastructure.security.RequirePermission;
import org.hexarch.user.application.port.in.UsersUseCase;
import org.hexarch.user.infrastructure.adapters.in.rest.dto.ChangeRoleDto;
import org.hexarch.user.infrastructure.adapters.in.rest.dto.UserDto;

import io.quarkus.security.Authenticated;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersController {

    private final UsersUseCase usersUseCase;

    public UsersController(UsersUseCase usersUseCase) {
        this.usersUseCase = usersUseCase;
    }

    @GET
    @Path("/{userId}")
    @Authenticated
    @ApiWraped(message = "user.found")
    public UserDto findById(@PathParam("userId") UUID userId) {
        return UserDto.from(usersUseCase.findById(userId));
    }

    @PATCH
    @Path("/{userId}/ban")
    @RequirePermission(PlatformPermission.USER_BAN)
    @ApiWraped(message = "user.ban.success")
    public UserDto ban(@PathParam("userId") UUID userId) {
        return UserDto.from(usersUseCase.banUser(userId));
    }

    @PATCH
    @Path("/{userId}/unban")
    @RequirePermission(PlatformPermission.USER_BAN)
    @ApiWraped(message = "user.unban.success")
    public UserDto unban(@PathParam("userId") UUID userId) {
        return UserDto.from(usersUseCase.unbanUser(userId));
    }

    @PATCH
    @Path("/{userId}/role")
    @RequirePermission(PlatformPermission.USER_ROLE_ASSIGN)
    @ApiWraped(message = "user.role.success")
    public UserDto changeRole(@PathParam("userId") UUID userId, @Valid ChangeRoleDto request) {
        return UserDto.from(usersUseCase.changeRole(userId, request.role()));
    }
}
