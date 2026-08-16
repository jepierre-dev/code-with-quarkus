package org.hexarch.song.infrastructure.adapters.in.rest.dto;

import java.util.UUID;

import org.hexarch.song.domain.model.SongModel;

public record SongDto(UUID id, String title, String artist, String audioUrl, int durationSeconds) {

    public static SongDto from(SongModel song) {
        return new SongDto(song.id(), song.title(), song.artist(), song.audioUrl(), song.durationSeconds());
    }
}
