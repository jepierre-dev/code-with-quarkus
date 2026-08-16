package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import org.hexarch.level.domain.enums.LevelRole;
import org.hexarch.level.domain.model.LevelViewer;

/** Null en la respuesta cuando el que llama es anonimo. */
public record LevelViewerDto(boolean liked, LevelRole memberRole, boolean canEdit, boolean canPublish) {

    public static LevelViewerDto from(LevelViewer viewer) {
        return viewer == null
                ? null
                : new LevelViewerDto(viewer.liked(), viewer.memberRole(), viewer.canEdit(), viewer.canPublish());
    }
}
