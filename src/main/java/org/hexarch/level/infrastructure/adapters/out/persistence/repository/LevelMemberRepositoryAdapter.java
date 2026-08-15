package org.hexarch.level.infrastructure.adapters.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.application.port.out.LevelMemberRepositoryPort;
import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.model.LevelMemberModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelMemberEntity;
import org.hexarch.level.infrastructure.adapters.out.persistence.mapper.LevelMemberMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LevelMemberRepositoryAdapter implements LevelMemberRepositoryPort {

    private static final String BY_KEY = "id.levelId = ?1 and id.userId = ?2";

    @Override
    public LevelMemberModel save(LevelMemberModel member) {
        LevelMemberEntity managed = findEntity(member.levelId(), member.userId());
        if (managed == null) {
            LevelMemberEntity entity = LevelMemberMapper.toEntity(member);
            entity.persist();
            return LevelMemberMapper.toDomain(entity);
        }
        managed.role = member.role();
        return LevelMemberMapper.toDomain(managed);
    }

    @Override
    public Optional<LevelMemberModel> find(UUID levelId, UUID userId) {
        LevelMemberEntity entity = findEntity(levelId, userId);
        return entity == null ? Optional.empty() : Optional.of(LevelMemberMapper.toDomain(entity));
    }

    @Override
    public List<LevelMemberModel> findByLevelId(UUID levelId) {
        return LevelMemberEntity.<LevelMemberEntity>find("id.levelId", levelId).list().stream()
                .map(LevelMemberMapper::toDomain)
                .toList();
    }

    // Se resuelve por PK compuesta: es la consulta mas repetida del contexto.
    @Override
    public Optional<LevelRole> findRole(UUID levelId, UUID userId) {
        return LevelMemberEntity.<LevelMemberEntity>find(BY_KEY, levelId, userId)
                .firstResultOptional()
                .map(entity -> entity.role);
    }

    @Override
    public void remove(UUID levelId, UUID userId) {
        LevelMemberEntity.delete(BY_KEY, levelId, userId);
    }

    private static LevelMemberEntity findEntity(UUID levelId, UUID userId) {
        return LevelMemberEntity.find(BY_KEY, levelId, userId).firstResult();
    }
}
