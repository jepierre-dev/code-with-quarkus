package org.hexarch.song.domain.exceptions;

import org.hexarch.shared.domain.ErrorCode;

public enum SongErrorCode implements ErrorCode {

    SONG_NOT_FOUND("SONG-001");

    private final String code;

    SongErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }
}
