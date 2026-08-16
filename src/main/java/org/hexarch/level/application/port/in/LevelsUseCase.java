package org.hexarch.level.application.port.in;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.domain.model.DifficultyModel;
import org.hexarch.level.domain.model.LevelDetail;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.level.domain.model.LevelSearchCriteria;
import org.hexarch.level.domain.model.LevelSummary;
import org.hexarch.shared.domain.Page;
import org.hexarch.shared.domain.security.Caller;

/** Ciclo de vida del nivel. Caller nunca es null: los dos primeros metodos admiten anonimo. */
public interface LevelsUseCase {

    /** Catalogo publico: no varia por usuario, asi que no necesita Caller. */
    List<DifficultyModel> difficulties();

    Page<LevelSummary> search(Caller caller, LevelSearchCriteria criteria);

    LevelDetail view(Caller caller, UUID levelId);

    LevelModel create(Caller caller, String name, String description, UUID songId);

    LevelModel rename(Caller caller, UUID levelId, String name, String description);

    LevelModel publish(Caller caller, UUID levelId);

    LevelModel unpublish(Caller caller, UUID levelId);

    /** Calificar es moderacion: exige LEVEL_APPROVE, no ser miembro del nivel. */
    LevelModel rate(Caller caller, UUID levelId, UUID difficultyId);

    void delete(Caller caller, UUID levelId);
}
