package org.hexarch.song.infrastructure.adapters.in.rest;

import java.util.UUID;

import org.hexarch.shared.domain.Page;
import org.hexarch.shared.infrastructure.rest.ApiWraped;
import org.hexarch.song.application.port.in.SongsUseCase;
import org.hexarch.song.infrastructure.adapters.in.rest.dto.SongDto;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/songs")
@Produces(MediaType.APPLICATION_JSON)
public class SongsController {

    private final SongsUseCase songsUseCase;

    public SongsController(SongsUseCase songsUseCase) {
        this.songsUseCase = songsUseCase;
    }

    @GET
    @PermitAll
    @ApiWraped(message = "song.search.success")
    public Page<SongDto> search(
            @QueryParam("q") String query,
            @QueryParam("page") int page,
            @QueryParam("size") int size) {

        Page<org.hexarch.song.domain.model.SongModel> result = songsUseCase.search(query, page, size);
        return new Page<>(result.items().stream().map(SongDto::from).toList(),
                result.totalItems(), result.page(), result.size());
    }

    @GET
    @Path("/{songId}")
    @PermitAll
    @ApiWraped(message = "song.found")
    public SongDto findById(@PathParam("songId") UUID songId) {
        return SongDto.from(songsUseCase.findById(songId));
    }
}
