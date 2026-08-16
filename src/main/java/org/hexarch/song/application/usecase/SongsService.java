package org.hexarch.song.application.usecase;

import java.util.UUID;

import org.hexarch.shared.domain.Page;
import org.hexarch.song.application.port.in.SongsUseCase;
import org.hexarch.song.application.port.out.SongRepositoryPort;
import org.hexarch.song.domain.exceptions.SongErrors;
import org.hexarch.song.domain.model.SongModel;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SongsService implements SongsUseCase {

    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_SIZE = 20;

    private final SongRepositoryPort songRepository;

    public SongsService(SongRepositoryPort songRepository) {
        this.songRepository = songRepository;
    }

    // Sin tope, un listado publico paginado es un vector de agotamiento de recursos.
    @Override
    public Page<SongModel> search(String query, int page, int size) {
        return songRepository.search(
                query == null ? "" : query.trim(),
                Math.max(page, 0),
                size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE));
    }

    @Override
    public SongModel findById(UUID songId) {
        return songRepository.findById(songId).orElseThrow(() -> SongErrors.songNotFound(songId));
    }
}
