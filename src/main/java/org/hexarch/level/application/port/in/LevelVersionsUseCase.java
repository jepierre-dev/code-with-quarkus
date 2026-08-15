package org.hexarch.level.application.port.in;

import java.util.List;
import java.util.UUID;

import org.hexarch.level.domain.model.LevelDownload;
import org.hexarch.level.domain.model.LevelVersionModel;
import org.hexarch.shared.domain.security.Caller;

public interface LevelVersionsUseCase {

    /** El numero de version, el checksum y la longitud los calcula el caso de uso, no el cliente. */
    LevelVersionModel upload(Caller caller, UUID levelId, byte[] levelData, String changelog);

    List<LevelVersionModel> history(Caller caller, UUID levelId);

    /** Descargar cuenta como descarga, por eso exige identidad aunque el nivel sea publico. */
    LevelDownload download(Caller caller, UUID levelId);
}
