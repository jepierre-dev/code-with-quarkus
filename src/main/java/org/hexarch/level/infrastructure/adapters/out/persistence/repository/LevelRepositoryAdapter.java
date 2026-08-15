package org.hexarch.level.infrastructure.adapters.out.persistence.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.application.port.out.LevelRepositoryPort;
import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.level.domain.exceptions.LevelErrors;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.level.domain.model.LevelSearchCriteria;
import org.hexarch.level.domain.model.LevelSummary;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelEntity;
import org.hexarch.level.infrastructure.adapters.out.persistence.mapper.LevelMapper;
import org.hexarch.shared.domain.Page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@ApplicationScoped
public class LevelRepositoryAdapter implements LevelRepositoryPort {

    private static final String SUMMARY_COLUMNS = """
            l.id, l.name, l.status::text, l.difficulty_id, l.length,
            COALESCE(s.likes, 0), COALESCE(s.downloads, 0), l.published_at
            """;

    private final EntityManager entityManager;

    public LevelRepositoryAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public LevelModel create(LevelModel level) {
        LevelEntity entity = LevelMapper.toEntity(level);
        entity.persist();
        return LevelMapper.toDomain(entity);
    }

    // Muta la instancia gestionada: Hibernate emite el UPDATE por dirty checking al hacer flush.
    @Override
    public LevelModel update(LevelModel level) {
        LevelEntity managed = LevelEntity.findById(level.id());
        if (managed == null) {
            throw LevelErrors.levelNotFound(level.id());
        }
        LevelMapper.copyInto(managed, level);
        return LevelMapper.toDomain(managed);
    }

    @Override
    public Optional<LevelModel> findById(UUID levelId) {
        LevelEntity entity = LevelEntity.findById(levelId);
        return entity == null ? Optional.empty() : Optional.of(LevelMapper.toDomain(entity));
    }

    @Override
    public Page<LevelSummary> search(LevelSearchCriteria criteria) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        String where = whereOf(criteria, parameters);

        long total = ((Number) bind(entityManager.createNativeQuery(
                "SELECT count(*) FROM levels l" + where), parameters).getSingleResult()).longValue();
        if (total == 0) {
            return new Page<>(List.of(), 0, criteria.page(), criteria.size());
        }

        Query query = bind(entityManager.createNativeQuery("""
                SELECT %s FROM levels l
                LEFT JOIN level_stats s ON s.level_id = l.id
                %s
                ORDER BY l.published_at DESC NULLS LAST, l.created_at DESC
                LIMIT :size OFFSET :offset
                """.formatted(SUMMARY_COLUMNS, where), Object[].class), parameters);
        query.setParameter("size", criteria.size());
        query.setParameter("offset", (long) criteria.page() * criteria.size());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        List<LevelSummary> items = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            items.add(toSummary(row));
        }
        return new Page<>(items, total, criteria.page(), criteria.size());
    }

    @Override
    public boolean existsById(UUID levelId) {
        return LevelEntity.count("id", levelId) > 0;
    }

    /**
     * El WHERE se arma con fragmentos fijos y valores siempre enlazados; nada de la peticion
     * se concatena en el SQL.
     */
    private static String whereOf(LevelSearchCriteria criteria, Map<String, Object> parameters) {
        List<String> conditions = new ArrayList<>();
        conditions.add("l.status <> 'DELETED'");

        if (!criteria.query().isEmpty()) {
            conditions.add("l.name ILIKE :namePattern");
            parameters.put("namePattern", "%" + criteria.query() + "%");
        }
        if (criteria.difficultyId() != null) {
            conditions.add("l.difficulty_id = :difficultyId");
            parameters.put("difficultyId", criteria.difficultyId());
        }
        if (criteria.status() != null) {
            conditions.add("l.status::text = :status");
            parameters.put("status", criteria.status().name());
        }
        if (criteria.authorId() != null) {
            conditions.add("""
                    EXISTS (SELECT 1 FROM level_members m
                            WHERE m.level_id = l.id AND m.user_id = :authorId AND m.role = 'OWNER')
                    """);
            parameters.put("authorId", criteria.authorId());
        }
        return " WHERE " + String.join(" AND ", conditions);
    }

    private static Query bind(Query query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
        return query;
    }

    private static LevelSummary toSummary(Object[] row) {
        Timestamp publishedAt = (Timestamp) row[7];
        return new LevelSummary(
                (UUID) row[0],
                (String) row[1],
                LevelStatus.valueOf((String) row[2]),
                (UUID) row[3],
                row[4] == null ? null : ((Number) row[4]).shortValue(),
                ((Number) row[5]).longValue(),
                ((Number) row[6]).longValue(),
                publishedAt == null ? null : publishedAt.toLocalDateTime());
    }
}
