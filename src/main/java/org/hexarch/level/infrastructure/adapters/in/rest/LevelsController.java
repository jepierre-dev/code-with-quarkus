package org.hexarch.level.infrastructure.adapters.in.rest;

import java.util.UUID;

import org.hexarch.level.application.port.in.LevelEngagementUseCase;
import org.hexarch.level.application.port.in.LevelsUseCase;
import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.level.domain.model.LevelSearchCriteria;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.CreateLevelDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.LevelDetailDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.LevelDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.LevelStatsDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.LevelSummaryDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.RateLevelDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.UpdateLevelDto;
import org.hexarch.shared.domain.Page;
import org.hexarch.shared.domain.security.PlatformPermission;
import org.hexarch.shared.infrastructure.rest.ApiWraped;
import org.hexarch.shared.infrastructure.security.CallerResolver;
import org.hexarch.shared.infrastructure.security.RequirePermission;

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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

// @Consumes va por metodo, no en la clase: un POST sin cuerpo contra un @Consumes de clase responde 415.
@Path("/levels")
@Produces(MediaType.APPLICATION_JSON)
public class LevelsController {

    private final LevelsUseCase levelsUseCase;
    private final LevelEngagementUseCase engagementUseCase;
    private final CallerResolver callerResolver;

    public LevelsController(LevelsUseCase levelsUseCase, LevelEngagementUseCase engagementUseCase,
            CallerResolver callerResolver) {
        this.levelsUseCase = levelsUseCase;
        this.engagementUseCase = engagementUseCase;
        this.callerResolver = callerResolver;
    }

    // Publico: el listado es el escaparate del juego. El caso de uso recorta lo que no puedes ver.
    @GET
    @PermitAll
    @ApiWraped(message = "level.search.success")
    public Page<LevelSummaryDto> search(
            @QueryParam("q") String query,
            @QueryParam("difficultyId") UUID difficultyId,
            @QueryParam("authorId") UUID authorId,
            @QueryParam("status") LevelStatus status,
            @QueryParam("page") int page,
            @QueryParam("size") int size) {

        Page<org.hexarch.level.domain.model.LevelSummary> result = levelsUseCase.search(callerResolver.current(),
                new LevelSearchCriteria(query, difficultyId, authorId, status, page, size));

        return new Page<>(result.items().stream().map(LevelSummaryDto::from).toList(),
                result.totalItems(), result.page(), result.size());
    }

    // Autenticacion opcional: con token la respuesta trae el bloque viewer.
    @GET
    @Path("/{levelId}")
    @PermitAll
    @ApiWraped(message = "level.found")
    public LevelDetailDto view(@PathParam("levelId") UUID levelId) {
        return LevelDetailDto.from(levelsUseCase.view(callerResolver.current(), levelId));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @ApiWraped(message = "level.created")
    public LevelDto create(@Valid CreateLevelDto request) {
        return LevelDto.from(levelsUseCase.create(callerResolver.current(), request.name(), request.description(),
                request.songId()));
    }

    @PATCH
    @Path("/{levelId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @ApiWraped(message = "level.updated")
    public LevelDto rename(@PathParam("levelId") UUID levelId, @Valid UpdateLevelDto request) {
        return LevelDto.from(levelsUseCase.rename(callerResolver.current(), levelId, request.name(),
                request.description()));
    }

    @POST
    @Path("/{levelId}/publish")
    @Authenticated
    @ApiWraped(message = "level.published")
    public LevelDto publish(@PathParam("levelId") UUID levelId) {
        return LevelDto.from(levelsUseCase.publish(callerResolver.current(), levelId));
    }

    @POST
    @Path("/{levelId}/unpublish")
    @Authenticated
    @ApiWraped(message = "level.unpublished")
    public LevelDto unpublish(@PathParam("levelId") UUID levelId) {
        return LevelDto.from(levelsUseCase.unpublish(callerResolver.current(), levelId));
    }

    // Primera barrera; el caso de uso reafirma el mismo permiso.
    @PATCH
    @Path("/{levelId}/difficulty")
    @Consumes(MediaType.APPLICATION_JSON)
    @RequirePermission(PlatformPermission.LEVEL_APPROVE)
    @ApiWraped(message = "level.rated")
    public LevelDto rate(@PathParam("levelId") UUID levelId, @Valid RateLevelDto request) {
        return LevelDto.from(levelsUseCase.rate(callerResolver.current(), levelId, request.difficultyId()));
    }

    @DELETE
    @Path("/{levelId}")
    @Authenticated
    public void delete(@PathParam("levelId") UUID levelId) {
        levelsUseCase.delete(callerResolver.current(), levelId);
    }

    @POST
    @Path("/{levelId}/likes")
    @Authenticated
    @ApiWraped(message = "level.liked")
    public LevelStatsDto like(@PathParam("levelId") UUID levelId) {
        return LevelStatsDto.from(engagementUseCase.like(callerResolver.current(), levelId));
    }

    @DELETE
    @Path("/{levelId}/likes")
    @Authenticated
    @ApiWraped(message = "level.unliked")
    public LevelStatsDto unlike(@PathParam("levelId") UUID levelId) {
        return LevelStatsDto.from(engagementUseCase.unlike(callerResolver.current(), levelId));
    }

    @POST
    @Path("/{levelId}/plays")
    @Authenticated
    public void registerPlay(@PathParam("levelId") UUID levelId) {
        engagementUseCase.registerPlay(callerResolver.current(), levelId);
    }
}
