package org.hexarch.shared.domain.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PlatformRoleTest {

    @Test
    void el_jugador_no_tiene_ningun_permiso_de_plataforma() {
        assertTrue(PlatformRole.PLAYER.permissions().isEmpty());
    }

    @Test
    void el_moderador_banea_y_califica_pero_no_reparte_roles() {
        assertTrue(PlatformRole.MODERATOR.has(PlatformPermission.USER_BAN));
        assertTrue(PlatformRole.MODERATOR.has(PlatformPermission.LEVEL_APPROVE));
        assertFalse(PlatformRole.MODERATOR.has(PlatformPermission.USER_ROLE_ASSIGN));
        assertFalse(PlatformRole.MODERATOR.has(PlatformPermission.LEVEL_DELETE_ANY));
    }

    @Test
    void el_admin_tiene_todos() {
        assertEquals(PlatformPermission.values().length, PlatformRole.ADMIN.permissions().size());
    }

    // Un token con un rol desconocido debe caer al minimo privilegio, nunca fallar.
    @Test
    void un_grupo_desconocido_degrada_a_jugador() {
        assertEquals(PlatformRole.PLAYER, PlatformRole.fromGroups(Set.of("SUPERUSER")));
        assertEquals(PlatformRole.PLAYER, PlatformRole.fromGroups(Set.of()));
        assertEquals(PlatformRole.PLAYER, PlatformRole.fromGroups(null));
    }

    @Test
    void reconoce_el_rol_del_claim_groups() {
        assertEquals(PlatformRole.ADMIN, PlatformRole.fromGroups(List.of("ADMIN")));
        assertEquals(PlatformRole.MODERATOR, PlatformRole.fromGroups(List.of("MODERATOR")));
    }

    @Test
    void el_permiso_no_se_puede_alterar_desde_fuera() {
        Set<PlatformPermission> permisos = PlatformRole.ADMIN.permissions();
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> permisos.remove(PlatformPermission.USER_BAN));
    }

    @Test
    void el_caller_anonimo_no_tiene_identidad_ni_permisos() {
        assertTrue(Caller.ANONYMOUS.isAnonymous());
        assertFalse(Caller.ANONYMOUS.has(PlatformPermission.USER_BAN));
    }

    @Test
    void requireUserId_falla_para_el_anonimo() {
        assertEquals("AUTHZ-002", org.junit.jupiter.api.Assertions
                .assertThrows(org.hexarch.shared.domain.DomainException.class, Caller.ANONYMOUS::requireUserId)
                .errorCode().code());
    }

    @Test
    void is_compara_la_identidad_sin_estallar_con_nulos() {
        UUID userId = UUID.randomUUID();
        Caller caller = new Caller(userId, PlatformRole.PLAYER);

        assertTrue(caller.is(userId));
        assertFalse(caller.is(UUID.randomUUID()));
        assertFalse(caller.is(null));
        assertFalse(Caller.ANONYMOUS.is(userId));
    }
}
