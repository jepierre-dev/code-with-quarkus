package org.hexarch.level.infrastructure.adapters.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.application.port.out.LevelHistoryRepositoryPort;
import org.hexarch.level.domain.model.LevelHistoryModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelHistoryEntity;
import org.hexarch.level.infrastructure.adapters.out.persistence.mapper.LevelHistoryMapper;

import io.quarkus.panache.common.Page;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LevelHistoryRepositoryAdapter implements LevelHistoryRepositoryPort {

    @Override
    public void append(LevelHistoryModel entry) {
        LevelHistoryMapper.toEntity(entry).persist();
    }

    @Override
    public List<LevelHistoryModel> findByLevelId(UUID levelId, int limit) {
        return LevelHistoryEntity.<LevelHistoryEntity>find("levelId = ?1 order by createdAt desc", levelId)
                .page(Page.ofSize(limit))
                .list().stream()
                .map(LevelHistoryMapper::toDomain)
                .toList();
    }
}
