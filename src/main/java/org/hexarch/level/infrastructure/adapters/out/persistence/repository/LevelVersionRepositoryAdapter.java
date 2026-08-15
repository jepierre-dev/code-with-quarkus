package org.hexarch.level.infrastructure.adapters.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.application.port.out.LevelVersionRepositoryPort;
import org.hexarch.level.domain.model.LevelVersionModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelVersionEntity;
import org.hexarch.level.infrastructure.adapters.out.persistence.mapper.LevelVersionMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class LevelVersionRepositoryAdapter implements LevelVersionRepositoryPort {

    private final EntityManager entityManager;

    public LevelVersionRepositoryAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public LevelVersionModel create(LevelVersionModel version, byte[] levelData) {
        LevelVersionEntity entity = LevelVersionMapper.toEntity(version, levelData);
        entity.persist();
        return LevelVersionMapper.toDomain(entity);
    }

    @Override
    public Optional<LevelVersionModel> findById(UUID versionId) {
        LevelVersionEntity entity = LevelVersionEntity.findById(versionId);
        return entity == null ? Optional.empty() : Optional.of(LevelVersionMapper.toDomain(entity));
    }

    @Override
    public List<LevelVersionModel> findByLevelId(UUID levelId) {
        return LevelVersionEntity.<LevelVersionEntity>find("levelId = ?1 order by versionNumber desc", levelId)
                .list().stream()
                .map(LevelVersionMapper::toDomain)
                .toList();
    }

    // Proyeccion directa a la columna: cargar la entidad traeria el blob aunque solo se pida el checksum.
    @Override
    public Optional<byte[]> findDataById(UUID versionId) {
        @SuppressWarnings("unchecked")
        List<byte[]> result = entityManager
                .createNativeQuery("SELECT level_data FROM level_versions WHERE id = ?1", byte[].class)
                .setParameter(1, versionId)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public int nextVersionNumber(UUID levelId) {
        Number max = (Number) entityManager
                .createNativeQuery("SELECT COALESCE(MAX(version_number), 0) FROM level_versions WHERE level_id = ?1")
                .setParameter(1, levelId)
                .getSingleResult();
        return max.intValue() + 1;
    }
}
