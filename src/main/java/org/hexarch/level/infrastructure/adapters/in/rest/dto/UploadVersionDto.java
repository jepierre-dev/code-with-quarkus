package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** levelData viaja en base64: Jackson lo decodifica solo al mapearlo a byte[]. */
public record UploadVersionDto(
        @NotNull byte[] levelData,
        @Size(max = 1000) String changelog,
        @Positive short length) {
}
