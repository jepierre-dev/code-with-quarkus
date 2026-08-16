package org.hexarch.level.application.port.out;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.level.domain.model.LevelSearchCriteria;
import org.hexarch.level.domain.model.LevelSummary;
import org.hexarch.shared.domain.Page;

public interface LevelRepositoryPort {

    LevelModel create(LevelModel level);

    LevelModel update(LevelModel level);

    Optional<LevelModel> findById(UUID levelId);

    /**
     * Bloquea la fila del nivel hasta el fin de la transaccion. Solo para operaciones que
     * calculan algo a partir de sus filas hijas, como el numero de la siguiente version.
     */
    Optional<LevelModel> findByIdForUpdate(UUID levelId);

    /** Devuelve resumenes, no agregados: un listado no debe cargar el nivel entero por fila. */
    Page<LevelSummary> search(LevelSearchCriteria criteria);

    boolean existsById(UUID levelId);
}
