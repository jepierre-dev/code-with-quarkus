package org.hexarch.level.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class LevelModelTest {

    private static final UUID SONG_ID = UUID.randomUUID();
    private static final UUID VERSION_ID = UUID.randomUUID();
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 8, 15, 12, 0);

    private static String codeOf(Executable action) {
        return assertThrows(DomainException.class, action).errorCode().code();
    }

    @Nested
    @DisplayName("creacion")
    class Creacion {

        @Test
        void nace_como_borrador_sin_dificultad_ni_longitud() {
            LevelModel level = LevelModel.create("Bloodbath", "un clasico", SONG_ID);

            assertNotNull(level.id());
            assertEquals(LevelStatus.DRAFT, level.status());
            assertNull(level.difficultyId());
            assertNull(level.length());
            assertNull(level.currentVersionId());
            assertNull(level.publishedAt());
            assertFalse(level.isRated());
            assertFalse(level.isPublic());
            assertFalse(level.isPlayable());
        }

        @Test
        void normaliza_la_descripcion_ausente_a_cadena_vacia() {
            assertEquals("", LevelModel.create("Bloodbath", null, SONG_ID).description());
        }

        @Test
        void recorta_los_espacios_del_nombre() {
            assertEquals("Bloodbath", LevelModel.create("  Bloodbath  ", null, SONG_ID).name());
        }

        @Test
        void rechaza_un_nombre_demasiado_corto() {
            assertEquals("LEVEL-002", codeOf(() -> LevelModel.create("ab", null, SONG_ID)));
        }

        @Test
        void rechaza_un_nombre_demasiado_largo() {
            assertEquals("LEVEL-002", codeOf(() -> LevelModel.create("x".repeat(65), null, SONG_ID)));
        }
    }

    @Nested
    @DisplayName("invariantes de publicacion")
    class Invariantes {

        @Test
        void un_nivel_publicado_no_puede_estar_sin_version() {
            assertEquals("LEVEL-007", codeOf(() -> new LevelModel(UUID.randomUUID(), "Bloodbath", "", SONG_ID,
                    null, LevelStatus.PUBLISHED, (short) 10, AHORA, AHORA, null)));
        }

        @Test
        void un_nivel_publicado_no_puede_estar_sin_fecha() {
            assertEquals("LEVEL-008", codeOf(() -> new LevelModel(UUID.randomUUID(), "Bloodbath", "", SONG_ID,
                    null, LevelStatus.PUBLISHED, (short) 10, AHORA, null, VERSION_ID)));
        }
    }

    @Nested
    @DisplayName("transiciones")
    class Transiciones {

        @Test
        void publicar_sin_version_falla() {
            assertEquals("LEVEL-007", codeOf(() -> LevelModel.create("Bloodbath", null, SONG_ID).publish(AHORA)));
        }

        @Test
        void publicar_con_version_fija_estado_y_fecha() {
            LevelModel publicado = LevelModel.create("Bloodbath", null, SONG_ID)
                    .withCurrentVersion(VERSION_ID, (short) 90)
                    .publish(AHORA);

            assertEquals(LevelStatus.PUBLISHED, publicado.status());
            assertEquals(AHORA, publicado.publishedAt());
            assertTrue(publicado.isPublic());
            assertTrue(publicado.isPlayable());
        }

        // publishedAt es "cuando se hizo publico por primera vez", no "esta publico ahora".
        @Test
        void republicar_conserva_la_fecha_original() {
            LevelModel publicado = LevelModel.create("Bloodbath", null, SONG_ID)
                    .withCurrentVersion(VERSION_ID, (short) 90)
                    .publish(AHORA);

            LevelModel republicado = publicado.unpublish().publish(AHORA.plusDays(30));

            assertEquals(AHORA, republicado.publishedAt());
        }

        @Test
        void despublicar_pasa_a_unlisted_y_conserva_la_fecha() {
            LevelModel despublicado = LevelModel.create("Bloodbath", null, SONG_ID)
                    .withCurrentVersion(VERSION_ID, (short) 90)
                    .publish(AHORA)
                    .unpublish();

            assertEquals(LevelStatus.UNLISTED, despublicado.status());
            assertEquals(AHORA, despublicado.publishedAt());
        }

        @Test
        void la_longitud_solo_llega_con_la_version() {
            LevelModel borrador = LevelModel.create("Bloodbath", null, SONG_ID);
            assertNull(borrador.length());

            assertEquals((short) 90, borrador.withCurrentVersion(VERSION_ID, (short) 90).length());
        }

        // Calificar es moderacion: no toca el estado de publicacion.
        @Test
        void calificar_no_publica_ni_despublica() {
            UUID difficultyId = UUID.randomUUID();
            LevelModel calificado = LevelModel.create("Bloodbath", null, SONG_ID).rate(difficultyId);

            assertEquals(LevelStatus.DRAFT, calificado.status());
            assertEquals(difficultyId, calificado.difficultyId());
            assertTrue(calificado.isRated());
        }

        @Test
        void borrar_es_logico() {
            assertEquals(LevelStatus.DELETED, LevelModel.create("Bloodbath", null, SONG_ID).markDeleted().status());
        }

        @Test
        void las_transiciones_conservan_el_id() {
            LevelModel original = LevelModel.create("Bloodbath", null, SONG_ID);
            assertSame(original.id(), original.rename("Otro nombre", "x").id());
        }
    }
}
