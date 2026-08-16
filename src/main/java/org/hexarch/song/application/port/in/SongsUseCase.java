package org.hexarch.song.application.port.in;

import java.util.UUID;

import org.hexarch.shared.domain.Page;
import org.hexarch.song.domain.model.SongModel;

/** Catalogo publico de solo lectura: las canciones se siembran, no se crean en runtime. */
public interface SongsUseCase {

    Page<SongModel> search(String query, int page, int size);

    /** Lanza SONG-001 si no existe; es la validacion que usa el contexto level. */
    SongModel findById(UUID songId);
}
