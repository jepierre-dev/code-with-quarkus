package org.hexarch.level.application.port.in;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.model.LevelMemberModel;
import org.hexarch.shared.domain.security.Caller;

/** Eje de autorizacion por recurso: quien puede que sobre un nivel concreto. */
public interface LevelMembersUseCase {

    List<LevelMemberModel> members(Caller caller, UUID levelId);

    LevelMemberModel invite(Caller caller, UUID levelId, UUID userId, LevelRole role);

    LevelMemberModel changeRole(Caller caller, UUID levelId, UUID userId, LevelRole role);

    void remove(Caller caller, UUID levelId, UUID userId);

    /** Salir es distinto de que te echen: el OWNER no puede abandonar su propio nivel. */
    void leave(Caller caller, UUID levelId);
}
