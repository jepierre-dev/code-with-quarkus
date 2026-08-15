package org.hexarch.level.application.port.out;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.domain.model.LevelHistoryModel;

/** Append-only a proposito: el historial no se edita ni se borra, solo crece. */
public interface LevelHistoryRepositoryPort {

    void append(LevelHistoryModel entry);

    List<LevelHistoryModel> findByLevelId(UUID levelId, int limit);
}
