package org.hexarch.level.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Catalogo de dificultades. Un nivel no tiene ninguna hasta que un moderador lo califica. */
public record DifficultyModel(
    UUID id,
    String name,
    long stars,
    String icon
) {

    public DifficultyModel {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(icon, "icon must not be null");
    }
}
