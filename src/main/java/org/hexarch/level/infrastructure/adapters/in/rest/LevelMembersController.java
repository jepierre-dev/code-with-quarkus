package org.hexarch.level.infrastructure.adapters.in.rest;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.application.port.in.LevelMembersUseCase;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.ChangeMemberRoleDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.InviteMemberDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.LevelMemberDto;
import org.hexarch.shared.infrastructure.rest.ApiWraped;
import org.hexarch.shared.infrastructure.security.CallerResolver;

import io.quarkus.security.Authenticated;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/levels/{levelId}/members")
@Produces(MediaType.APPLICATION_JSON)
public class LevelMembersController {

    private final LevelMembersUseCase membersUseCase;
    private final CallerResolver callerResolver;

    public LevelMembersController(LevelMembersUseCase membersUseCase, CallerResolver callerResolver) {
        this.membersUseCase = membersUseCase;
        this.callerResolver = callerResolver;
    }

    // Publico si el nivel lo es: son los creditos de la colaboracion.
    @GET
    @PermitAll
    @ApiWraped(message = "level.member.list")
    public List<LevelMemberDto> members(@PathParam("levelId") UUID levelId) {
        return membersUseCase.members(callerResolver.current(), levelId).stream()
                .map(LevelMemberDto::from)
                .toList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @ApiWraped(message = "level.member.invited")
    public LevelMemberDto invite(@PathParam("levelId") UUID levelId, @Valid InviteMemberDto request) {
        return LevelMemberDto.from(
                membersUseCase.invite(callerResolver.current(), levelId, request.userId(), request.role()));
    }

    @PATCH
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @ApiWraped(message = "level.member.role.changed")
    public LevelMemberDto changeRole(@PathParam("levelId") UUID levelId, @PathParam("userId") UUID userId,
            @Valid ChangeMemberRoleDto request) {
        return LevelMemberDto.from(
                membersUseCase.changeRole(callerResolver.current(), levelId, userId, request.role()));
    }

    // Ruta fija antes que la parametrizada: /me nunca debe caer en {userId}.
    @DELETE
    @Path("/me")
    @Authenticated
    public void leave(@PathParam("levelId") UUID levelId) {
        membersUseCase.leave(callerResolver.current(), levelId);
    }

    @DELETE
    @Path("/{userId}")
    @Authenticated
    public void remove(@PathParam("levelId") UUID levelId, @PathParam("userId") UUID userId) {
        membersUseCase.remove(callerResolver.current(), levelId, userId);
    }
}
