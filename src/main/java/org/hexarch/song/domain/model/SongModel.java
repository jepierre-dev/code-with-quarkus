package org.hexarch.song.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Catalogo de audio. Un nivel referencia una cancion, nunca la copia. */
public record SongModel(
    UUID id,
    String title,
    String artist,
    String audioUrl,
    int durationSeconds
) {

    public SongModel {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(artist, "artist must not be null");
        Objects.requireNonNull(audioUrl, "audioUrl must not be null");
    }
}
