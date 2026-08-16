package org.hexarch.level.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.application.port.out.LevelMemberRepositoryPort;
import org.hexarch.level.domain.enums.LevelPermission;
import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.level.domain.model.LevelMemberModel;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.shared.domain.DomainException;
import org.hexarch.shared.domain.security.Caller;
import org.hexarch.shared.domain.security.PlatformRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LevelAccessGuardTest {

    private static final UUID SONG_ID = UUID.randomUUID();
    private static final UUID VERSION_ID = UUID.randomUUID();
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 8, 15, 12, 0);

    private static final Caller ANONIMO = Caller.ANONYMOUS;
    private static final Caller JUGADOR = new Caller(UUID.randomUUID(), PlatformRole.PLAYER);
    private static final Caller MODERADOR = new Caller(UUID.randomUUID(), PlatformRole.MODERATOR);
    private static final Caller ADMIN = new Caller(UUID.randomUUID(), PlatformRole.ADMIN);

    private MembersStub members;
    private LevelAccessGuard guard;

    @BeforeEach
    void setUp() {
        members = new MembersStub();
        guard = new LevelAccessGuard(members);
    }

    private static LevelModel borrador() {
        return LevelModel.create("Bloodbath", null, SONG_ID);
    }

    private static LevelModel con(LevelStatus status) {
        LevelModel publicable = borrador().withCurrentVersion(VERSION_ID, (short) 90).publish(AHORA);
        return switch (status) {
            case PUBLISHED -> publicable;
            case UNLISTED -> publicable.unpublish();
            case DELETED -> publicable.markDeleted();
            case DRAFT -> borrador();
        };
    }

    private static String codeOf(LevelModel level, Caller caller, LevelAccessGuard guard) {
        return assertThrows(DomainException.class, () -> guard.requireVisible(caller, level)).errorCode().code();
    }

    @Nested
    @DisplayName("visibilidad")
    class Visibilidad {

        @Test
        void cualquiera_ve_un_nivel_publicado() {
            guard.requireVisible(ANONIMO, con(LevelStatus.PUBLISHED));
        }

        // UNLISTED se ve con el enlace directo; lo que no hace es salir en las busquedas.
        @Test
        void cualquiera_ve_un_nivel_unlisted_si_tiene_el_enlace() {
            guard.requireVisible(ANONIMO, con(LevelStatus.UNLISTED));
        }

        // 404 y no 403: un 403 confirmaria que ese id existe.
        @Test
        void un_borrador_ajeno_se_comporta_como_inexistente() {
            assertEquals("LEVEL-001", codeOf(borrador(), ANONIMO, guard));
            assertEquals("LEVEL-001", codeOf(borrador(), JUGADOR, guard));
        }

        @Test
        void un_miembro_ve_el_borrador() {
            LevelModel level = borrador();
            members.asigna(level.id(), JUGADOR.userId(), LevelRole.VIEWER);

            guard.requireVisible(JUGADOR, level);
        }

        @Test
        void un_nivel_borrado_no_lo_ve_nadie() {
            LevelModel borrado = con(LevelStatus.DELETED);
            members.asigna(borrado.id(), JUGADOR.userId(), LevelRole.OWNER);

            assertEquals("LEVEL-001", codeOf(borrado, JUGADOR, guard));
        }

        @Test
        void la_moderacion_ve_borradores_ajenos() {
            guard.requireVisible(MODERADOR, borrador());
            guard.requireVisible(MODERADOR, con(LevelStatus.DELETED));
        }
    }

    @Nested
    @DisplayName("permisos por recurso")
    class Permisos {

        @Test
        void el_anonimo_no_puede_nada() {
            assertFalse(guard.can(ANONIMO, UUID.randomUUID(), LevelPermission.VIEW_DRAFT));
        }

        @Test
        void el_dueno_puede_todo() {
            UUID levelId = UUID.randomUUID();
            members.asigna(levelId, JUGADOR.userId(), LevelRole.OWNER);

            for (LevelPermission permission : LevelPermission.values()) {
                assertTrue(guard.can(JUGADOR, levelId, permission), permission.name());
            }
        }

        @Test
        void el_decorador_sube_versiones_pero_no_publica() {
            UUID levelId = UUID.randomUUID();
            members.asigna(levelId, JUGADOR.userId(), LevelRole.DECORATOR);

            assertTrue(guard.can(JUGADOR, levelId, LevelPermission.UPLOAD_VERSION));
            assertFalse(guard.can(JUGADOR, levelId, LevelPermission.PUBLISH));
            assertFalse(guard.can(JUGADOR, levelId, LevelPermission.EDIT));
        }

        @Test
        void el_verificador_solo_mira() {
            UUID levelId = UUID.randomUUID();
            members.asigna(levelId, JUGADOR.userId(), LevelRole.VERIFIER);

            assertTrue(guard.can(JUGADOR, levelId, LevelPermission.VIEW_DRAFT));
            assertFalse(guard.can(JUGADOR, levelId, LevelPermission.UPLOAD_VERSION));
        }

        @Test
        void require_lanza_el_codigo_del_eje_de_recurso() {
            DomainException error = assertThrows(DomainException.class,
                    () -> guard.require(JUGADOR, UUID.randomUUID(), LevelPermission.PUBLISH));

            assertEquals("LEVEL-009", error.errorCode().code());
        }

        // El permiso de plataforma pasa por encima de la membresia, pero solo para borrar.
        @Test
        void el_admin_borra_niveles_ajenos_sin_ser_miembro() {
            UUID levelId = UUID.randomUUID();

            assertTrue(guard.can(ADMIN, levelId, LevelPermission.DELETE));
            assertFalse(guard.can(ADMIN, levelId, LevelPermission.EDIT));
        }
    }

    @Nested
    @DisplayName("memoizacion por peticion")
    class Memoizacion {

        @Test
        void el_rol_se_consulta_una_sola_vez_por_nivel() {
            UUID levelId = UUID.randomUUID();
            members.asigna(levelId, JUGADOR.userId(), LevelRole.OWNER);

            guard.roleOf(JUGADOR, levelId);
            guard.can(JUGADOR, levelId, LevelPermission.EDIT);
            guard.can(JUGADOR, levelId, LevelPermission.PUBLISH);

            assertEquals(1, members.consultas);
        }

        @Test
        void tambien_memoiza_la_ausencia_de_rol() {
            UUID levelId = UUID.randomUUID();

            guard.can(JUGADOR, levelId, LevelPermission.EDIT);
            guard.can(JUGADOR, levelId, LevelPermission.PUBLISH);

            assertEquals(1, members.consultas);
        }

        @Test
        void niveles_distintos_no_comparten_memo() {
            guard.can(JUGADOR, UUID.randomUUID(), LevelPermission.EDIT);
            guard.can(JUGADOR, UUID.randomUUID(), LevelPermission.EDIT);

            assertEquals(2, members.consultas);
        }
    }

    /** Doble del puerto out; cuenta las consultas para poder verificar la memoizacion. */
    private static final class MembersStub implements LevelMemberRepositoryPort {

        private final java.util.Map<String, LevelRole> roles = new java.util.HashMap<>();
        private int consultas;

        void asigna(UUID levelId, UUID userId, LevelRole role) {
            roles.put(levelId + "|" + userId, role);
        }

        @Override
        public Optional<LevelRole> findRole(UUID levelId, UUID userId) {
            consultas++;
            return Optional.ofNullable(roles.get(levelId + "|" + userId));
        }

        @Override
        public LevelMemberModel save(LevelMemberModel member) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<LevelMemberModel> find(UUID levelId, UUID userId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<LevelMemberModel> findByLevelId(UUID levelId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove(UUID levelId, UUID userId) {
            throw new UnsupportedOperationException();
        }
    }
}
