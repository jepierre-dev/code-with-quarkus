package org.hexarch.song.application.port.out;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.shared.domain.Page;
import org.hexarch.song.domain.model.SongModel;

public interface SongRepositoryPort {

    Optional<SongModel> findById(UUID songId);

    Page<SongModel> search(String query, int page, int size);
}
