package org.hexarch.song.domain.exceptions;

import java.util.Map;
import java.util.UUID;

import org.hexarch.shared.domain.DomainException;

public final class SongErrors {

    private SongErrors() {
    }

    public static DomainException songNotFound(UUID songId) {
        return new DomainException.NotFound(SongErrorCode.SONG_NOT_FOUND, Map.of("songId", songId));
    }
}
