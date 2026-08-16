package org.hexarch.song.infrastructure.adapters.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.shared.domain.Page;
import org.hexarch.song.application.port.out.SongRepositoryPort;
import org.hexarch.song.domain.model.SongModel;
import org.hexarch.song.infrastructure.adapters.out.persistence.entity.SongEntity;
import org.hexarch.song.infrastructure.adapters.out.persistence.mapper.SongMapper;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SongRepositoryAdapter implements SongRepositoryPort {

    @Override
    public Optional<SongModel> findById(UUID songId) {
        SongEntity entity = SongEntity.findById(songId);
        return entity == null ? Optional.empty() : Optional.of(SongMapper.toDomain(entity));
    }

    @Override
    public Page<SongModel> search(String query, int page, int size) {
        PanacheQuery<SongEntity> found = query.isEmpty()
                ? SongEntity.findAll(Sort.by("title"))
                : SongEntity.find("lower(title) like ?1 or lower(artist) like ?1", Sort.by("title"),
                        "%" + query.toLowerCase() + "%");

        long total = found.count();
        return new Page<>(
                found.page(io.quarkus.panache.common.Page.of(page, size)).list().stream()
                        .map(SongMapper::toDomain)
                        .toList(),
                total, page, size);
    }
}
