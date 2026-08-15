package org.hexarch.level.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.domain.model.DifficultyModel;

/** Catalogo de solo lectura: las dificultades se siembran por migracion, no se crean en runtime. */
public interface DifficultyRepositoryPort {

    List<DifficultyModel> findAll();

    Optional<DifficultyModel> findById(UUID difficultyId);
}
