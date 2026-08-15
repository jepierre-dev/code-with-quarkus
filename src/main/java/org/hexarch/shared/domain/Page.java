package org.hexarch.shared.domain;

import java.util.List;

/** Resultado paginado. El total viene aparte porque los listados publicos lo muestran. */
public record Page<T>(List<T> items, long totalItems, int page, int size) {

    public Page {
        items = List.copyOf(items);
    }

    public int totalPages() {
        return size <= 0 ? 0 : (int) Math.ceil((double) totalItems / size);
    }
}
