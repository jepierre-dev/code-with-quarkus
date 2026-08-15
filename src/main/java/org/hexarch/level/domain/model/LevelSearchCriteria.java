package org.hexarch.level.domain.model;

import java.util.UUID;

import org.hexarch.level.domain.enums.LevelStatus;

public record LevelSearchCriteria(
    String query,
    UUID difficultyId,
    UUID authorId,
    LevelStatus status,
    int page,
    int size
) {

    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_SIZE = 20;

    // El tamano se acota aqui: sin tope, un size grande convierte el listado publico en un vector de DoS.
    public LevelSearchCriteria {
        query = query == null ? "" : query.trim();
        page = Math.max(page, 0);
        size = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
    }
}
