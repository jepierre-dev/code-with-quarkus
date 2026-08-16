package org.hexarch.level.infrastructure.adapters.in.rest;

import java.util.List;

import org.hexarch.level.application.port.in.LevelsUseCase;
import org.hexarch.level.infrastructure.adapters.in.rest.dto.DifficultyDto;
import org.hexarch.shared.infrastructure.rest.ApiWraped;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// Fuera de /levels a proposito: /levels/difficulties chocaria con /levels/{levelId}.
@Path("/difficulties")
@Produces(MediaType.APPLICATION_JSON)
public class DifficultiesController {

    private final LevelsUseCase levelsUseCase;

    public DifficultiesController(LevelsUseCase levelsUseCase) {
        this.levelsUseCase = levelsUseCase;
    }

    @GET
    @PermitAll
    @ApiWraped(message = "level.difficulty.list")
    public List<DifficultyDto> difficulties() {
        return levelsUseCase.difficulties().stream()
                .map(DifficultyDto::from)
                .toList();
    }
}
