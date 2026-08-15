package org.hexarch.level.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.level.domain.model.LevelMemberModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelMemberEntity;

public final class LevelMemberMapper {

    private LevelMemberMapper() {
    }

    public static LevelMemberEntity toEntity(LevelMemberModel member) {
        LevelMemberEntity entity = new LevelMemberEntity();
        entity.id = new LevelMemberEntity.LevelMemberId(member.levelId(), member.userId());
        entity.role = member.role();
        entity.invitedBy = member.invitedBy();
        return entity;
    }

    public static LevelMemberModel toDomain(LevelMemberEntity entity) {
        return new LevelMemberModel(entity.id.levelId, entity.id.userId, entity.role, entity.joinedAt,
                entity.invitedBy);
    }
}
