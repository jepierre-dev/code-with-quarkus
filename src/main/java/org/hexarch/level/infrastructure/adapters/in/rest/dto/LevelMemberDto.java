package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.model.LevelMemberModel;

public record LevelMemberDto(
        UUID levelId,
        UUID userId,
        LevelRole role,
        LocalDateTime joinedAt,
        UUID invitedBy) {

    public static LevelMemberDto from(LevelMemberModel member) {
        return new LevelMemberDto(member.levelId(), member.userId(), member.role(), member.joinedAt(),
                member.invitedBy());
    }
}
