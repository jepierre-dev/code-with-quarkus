package org.hexarch.level.infrastructure.adapters.out.persistence.repository;

import java.util.UUID;

import org.hexarch.level.application.port.out.LevelStatsPort;
import org.hexarch.level.domain.model.LevelStatsModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelLikeEntity;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelStatsEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class LevelStatsRepositoryAdapter implements LevelStatsPort {

    // El incremento se resuelve en la base de datos: leer y escribir desde Java perderia escrituras concurrentes.
    private static final String INCREMENT_PLAYS = """
            INSERT INTO level_stats (level_id, plays) VALUES (?1, 1)
            ON CONFLICT (level_id) DO UPDATE SET plays = level_stats.plays + 1
            """;

    private static final String INCREMENT_DOWNLOADS = """
            INSERT INTO level_stats (level_id, downloads) VALUES (?1, 1)
            ON CONFLICT (level_id) DO UPDATE SET downloads = level_stats.downloads + 1
            """;

    private static final String INCREMENT_LIKES = """
            INSERT INTO level_stats (level_id, likes) VALUES (?1, 1)
            ON CONFLICT (level_id) DO UPDATE SET likes = level_stats.likes + 1
            """;

    private final EntityManager entityManager;

    public LevelStatsRepositoryAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(UUID levelId) {
        entityManager.createNativeQuery("""
                INSERT INTO level_stats (level_id) VALUES (?1)
                ON CONFLICT (level_id) DO NOTHING
                """)
                .setParameter(1, levelId)
                .executeUpdate();
    }

    @Override
    public LevelStatsModel findByLevelId(UUID levelId) {
        LevelStatsEntity entity = LevelStatsEntity.findById(levelId);
        return entity == null ? LevelStatsModel.ZERO : new LevelStatsModel(entity.downloads, entity.likes, entity.plays);
    }

    @Override
    public void registerPlay(UUID levelId) {
        execute(INCREMENT_PLAYS, levelId);
    }

    @Override
    public void registerDownload(UUID levelId) {
        execute(INCREMENT_DOWNLOADS, levelId);
    }

    // ON CONFLICT DO NOTHING hace el doble like idempotente sin depender de una lectura previa.
    @Override
    public boolean like(UUID levelId, UUID userId) {
        int inserted = entityManager.createNativeQuery("""
                INSERT INTO level_likes (level_id, user_id) VALUES (?1, ?2)
                ON CONFLICT (level_id, user_id) DO NOTHING
                """)
                .setParameter(1, levelId)
                .setParameter(2, userId)
                .executeUpdate();

        if (inserted == 0) {
            return false;
        }
        execute(INCREMENT_LIKES, levelId);
        return true;
    }

    @Override
    public boolean unlike(UUID levelId, UUID userId) {
        long deleted = LevelLikeEntity.delete("id.levelId = ?1 and id.userId = ?2", levelId, userId);
        if (deleted == 0) {
            return false;
        }
        // GREATEST evita un contador negativo si algun dia se borran likes por fuera.
        execute("UPDATE level_stats SET likes = GREATEST(likes - 1, 0) WHERE level_id = ?1", levelId);
        return true;
    }

    @Override
    public boolean hasLiked(UUID levelId, UUID userId) {
        return LevelLikeEntity.count("id.levelId = ?1 and id.userId = ?2", levelId, userId) > 0;
    }

    private void execute(String sql, UUID levelId) {
        entityManager.createNativeQuery(sql).setParameter(1, levelId).executeUpdate();
    }
}
