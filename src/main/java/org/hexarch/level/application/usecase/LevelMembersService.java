package org.hexarch.level.application.usecase;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.application.port.in.LevelMembersUseCase;
import org.hexarch.level.application.port.out.LevelHistoryRepositoryPort;
import org.hexarch.level.application.port.out.LevelMemberRepositoryPort;
import org.hexarch.level.application.port.out.LevelRepositoryPort;
import org.hexarch.level.domain.enums.LevelAction;
import org.hexarch.level.domain.enums.LevelPermission;
import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.exceptions.LevelErrors;
import org.hexarch.level.domain.model.LevelHistoryModel;
import org.hexarch.level.domain.model.LevelMemberModel;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.shared.domain.security.Caller;
import org.hexarch.user.application.port.in.UsersUseCase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LevelMembersService implements LevelMembersUseCase {

    private final LevelRepositoryPort levelRepository;
    private final LevelMemberRepositoryPort memberRepository;
    private final LevelHistoryRepositoryPort historyRepository;
    private final UsersUseCase usersUseCase;
    private final LevelAccessGuard guard;

    public LevelMembersService(LevelRepositoryPort levelRepository, LevelMemberRepositoryPort memberRepository,
            LevelHistoryRepositoryPort historyRepository, UsersUseCase usersUseCase, LevelAccessGuard guard) {
        this.levelRepository = levelRepository;
        this.memberRepository = memberRepository;
        this.historyRepository = historyRepository;
        this.usersUseCase = usersUseCase;
        this.guard = guard;
    }

    // Los creditos de una colaboracion son publicos si el nivel lo es.
    @Override
    public List<LevelMemberModel> members(Caller caller, UUID levelId) {
        guard.requireVisible(caller, requireLevel(levelId));
        return memberRepository.findByLevelId(levelId);
    }

    @Override
    @Transactional
    public LevelMemberModel invite(Caller caller, UUID levelId, UUID userId, LevelRole role) {
        UUID actorId = caller.requireUserId();
        requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.MANAGE_MEMBERS);
        requireAssignableRole(levelId, role);

        // Se comprueba por el puerto in del otro contexto: lanza USER-003 si no existe.
        usersUseCase.findById(userId);
        if (memberRepository.find(levelId, userId).isPresent()) {
            throw LevelErrors.memberAlreadyExists(levelId, userId);
        }

        LevelMemberModel member = memberRepository.save(
                new LevelMemberModel(levelId, userId, role, null, actorId));
        historyRepository.append(
                LevelHistoryModel.onMember(levelId, actorId, LevelAction.MEMBER_ADDED, userId));
        return member;
    }

    @Override
    @Transactional
    public LevelMemberModel changeRole(Caller caller, UUID levelId, UUID userId, LevelRole role) {
        UUID actorId = caller.requireUserId();
        requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.MANAGE_MEMBERS);
        requireAssignableRole(levelId, role);

        LevelMemberModel member = requireMember(levelId, userId);
        requireNotOwner(member);

        LevelMemberModel updated = memberRepository.save(member.withRole(role));
        historyRepository.append(
                LevelHistoryModel.onMember(levelId, actorId, LevelAction.ROLE_CHANGED, userId));
        return updated;
    }

    @Override
    @Transactional
    public void remove(Caller caller, UUID levelId, UUID userId) {
        UUID actorId = caller.requireUserId();
        requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.MANAGE_MEMBERS);

        requireNotOwner(requireMember(levelId, userId));
        memberRepository.remove(levelId, userId);
        historyRepository.append(
                LevelHistoryModel.onMember(levelId, actorId, LevelAction.MEMBER_REMOVED, userId));
    }

    @Override
    @Transactional
    public void leave(Caller caller, UUID levelId) {
        UUID userId = caller.requireUserId();
        requireLevel(levelId);

        requireNotOwner(requireMember(levelId, userId));
        memberRepository.remove(levelId, userId);
        historyRepository.append(
                LevelHistoryModel.onMember(levelId, userId, LevelAction.MEMBER_REMOVED, userId));
    }

    private LevelModel requireLevel(UUID levelId) {
        return levelRepository.findById(levelId).orElseThrow(() -> LevelErrors.levelNotFound(levelId));
    }

    private LevelMemberModel requireMember(UUID levelId, UUID userId) {
        return memberRepository.find(levelId, userId)
                .orElseThrow(() -> LevelErrors.memberNotFound(levelId, userId));
    }

    // El dueno no se toca: cambiar de dueno seria una transferencia, no una gestion de miembros.
    private void requireNotOwner(LevelMemberModel member) {
        if (member.role() == LevelRole.OWNER) {
            throw LevelErrors.ownerImmutable(member.levelId());
        }
    }

    private void requireAssignableRole(UUID levelId, LevelRole role) {
        if (role == LevelRole.OWNER) {
            throw LevelErrors.ownerImmutable(levelId);
        }
    }
}
