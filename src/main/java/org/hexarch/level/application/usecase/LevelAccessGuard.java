package org.hexarch.level.application.usecase;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.application.port.out.LevelMemberRepositoryPort;
import org.hexarch.level.domain.enums.LevelPermission;
import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.exceptions.LevelErrors;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.shared.domain.security.Caller;
import org.hexarch.shared.domain.security.PlatformPermission;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Eje de autorizacion por recurso. Vive en application porque necesita ir a BD:
 * el rol sobre un nivel no cabe en el JWT.
 */
@ApplicationScoped
public class LevelAccessGuard {

    private final LevelMemberRepositoryPort memberRepository;

    public LevelAccessGuard(LevelMemberRepositoryPort memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Optional<LevelRole> roleOf(Caller caller, UUID levelId) {
        return caller.isAnonymous() ? Optional.empty() : memberRepository.findRole(levelId, caller.userId());
    }

    public boolean can(Caller caller, UUID levelId, LevelPermission permission) {
        if (caller.isAnonymous()) {
            return false;
        }
        // La moderacion de plataforma pasa por encima de la membresia del nivel.
        if (permission == LevelPermission.DELETE && caller.has(PlatformPermission.LEVEL_DELETE_ANY)) {
            return true;
        }
        return roleOf(caller, levelId).filter(role -> role.has(permission)).isPresent();
    }

    public void require(Caller caller, UUID levelId, LevelPermission permission) {
        if (!can(caller, levelId, permission)) {
            throw LevelErrors.levelPermissionDenied(permission);
        }
    }

    /**
     * Un nivel no visible se comporta como inexistente: devolver 403 confirmaria que ese id existe
     * y convertiria los ids en un enumerador de borradores ajenos.
     */
    public void requireVisible(Caller caller, LevelModel level) {
        if (isVisible(caller, level)) {
            return;
        }
        throw LevelErrors.levelNotFound(level.id());
    }

    private boolean isVisible(Caller caller, LevelModel level) {
        if (caller.has(PlatformPermission.LEVEL_APPROVE)) {
            return true;
        }
        return switch (level.status()) {
            // UNLISTED se ve con el enlace directo, pero no aparece en las busquedas.
            case PUBLISHED, UNLISTED -> true;
            case DRAFT -> can(caller, level.id(), LevelPermission.VIEW_DRAFT);
            case DELETED -> false;
        };
    }
}
