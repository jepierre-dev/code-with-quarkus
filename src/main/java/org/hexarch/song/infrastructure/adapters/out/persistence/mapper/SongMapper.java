package org.hexarch.song.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.song.domain.model.SongModel;
import org.hexarch.song.infrastructure.adapters.out.persistence.entity.SongEntity;

public final class SongMapper {

    private SongMapper() {
    }

    public static SongModel toDomain(SongEntity entity) {
        return new SongModel(entity.id, entity.title, entity.artist, entity.audioUrl, entity.durationSeconds);
    }
}
