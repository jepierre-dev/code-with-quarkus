package org.hexarch.level.infrastructure.adapters.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.application.port.out.DifficultyRepositoryPort;
import org.hexarch.level.domain.model.DifficultyModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.DifficultyEntity;
import org.hexarch.level.infrastructure.adapters.out.persistence.mapper.DifficultyMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DifficultyRepositoryAdapter implements DifficultyRepositoryPort {

    @Override
    public List<DifficultyModel> findAll() {
        return DifficultyEntity.<DifficultyEntity>find("order by stars").list().stream()
                .map(DifficultyMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<DifficultyModel> findById(UUID difficultyId) {
        DifficultyEntity entity = DifficultyEntity.findById(difficultyId);
        return entity == null ? Optional.empty() : Optional.of(DifficultyMapper.toDomain(entity));
    }
}
