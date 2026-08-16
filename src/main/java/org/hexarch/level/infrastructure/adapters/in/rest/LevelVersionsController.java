package org.hexarch.level.infrastructure.adapters.in.rest;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.application.port.in.LevelVersionsUseCase;
import org.hexarch.level.domain.model.LevelDownload;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.LevelVersionDto;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.UploadVersionDto;
import org.hexarch.shared.infrastructure.rest.ApiWraped;
import org.hexarch.shared.infrastructure.security.CallerResolver;

import io.quarkus.security.Authenticated;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/levels/{levelId}/versions")
@Produces(MediaType.APPLICATION_JSON)
public class LevelVersionsController {

    private final LevelVersionsUseCase versionsUseCase;
    private final CallerResolver callerResolver;

    public LevelVersionsController(LevelVersionsUseCase versionsUseCase, CallerResolver callerResolver) {
        this.versionsUseCase = versionsUseCase;
        this.callerResolver = callerResolver;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Authenticated
    @ApiWraped(message = "level.version.created")
    public LevelVersionDto upload(@PathParam("levelId") UUID levelId, @Valid UploadVersionDto request) {
        return LevelVersionDto.from(versionsUseCase.upload(callerResolver.current(), levelId, request.levelData(),
                request.changelog(), request.length()));
    }

    @GET
    @Authenticated
    @ApiWraped(message = "level.version.history")
    public List<LevelVersionDto> history(@PathParam("levelId") UUID levelId) {
        return versionsUseCase.history(callerResolver.current(), levelId).stream()
                .map(LevelVersionDto::from)
                .toList();
    }

    // Sin @ApiWraped: la respuesta es el binario, no un sobre JSON.
    @GET
    @Path("/current")
    @Authenticated
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("levelId") UUID levelId) {
        LevelDownload download = versionsUseCase.download(callerResolver.current(), levelId);

        return Response.ok(download.data())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"level-%s-v%d.gdl\"".formatted(levelId, download.versionNumber()))
                .header("ETag", "\"%s\"".formatted(download.checksum()))
                .build();
    }
}
