package org.hexarch.level.domain.model;

/** El binario de una version, ya resuelto y listo para servir. */
public record LevelDownload(byte[] data, String checksum, int versionNumber) {
}
