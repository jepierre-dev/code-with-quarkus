package org.hexarch.level.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.model.LevelMemberModel;

public interface LevelMemberRepositoryPort {

    LevelMemberModel save(LevelMemberModel member);

    Optional<LevelMemberModel> find(UUID levelId, UUID userId);

    List<LevelMemberModel> findByLevelId(UUID levelId);

    /** Camino caliente de la autorizacion por recurso: se consulta en cada operacion sobre un nivel. */
    Optional<LevelRole> findRole(UUID levelId, UUID userId);

    void remove(UUID levelId, UUID userId);
}
