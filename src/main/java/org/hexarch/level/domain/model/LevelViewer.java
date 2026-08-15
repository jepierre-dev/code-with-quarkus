package org.hexarch.level.domain.model;

import org.hexarch.level.domain.enums.LevelRole;

/**
 * Lo que este usuario concreto puede hacer aqui. Es null para un visitante anonimo:
 * asi el mismo endpoint sirve a ambos sin duplicar contrato.
 */
public record LevelViewer(
    boolean liked,
    LevelRole memberRole,
    boolean canEdit,
    boolean canPublish
) {
}
