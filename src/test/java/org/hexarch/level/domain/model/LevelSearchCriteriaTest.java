package org.hexarch.level.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LevelSearchCriteriaTest {

    private static LevelSearchCriteria conPaginado(int page, int size) {
        return new LevelSearchCriteria(null, null, null, null, page, size);
    }

    // Sin tope, un listado publico es un vector de agotamiento de recursos.
    @Test
    void acota_el_tamano_de_pagina() {
        assertEquals(100, conPaginado(0, 100_000).size());
        assertEquals(100, conPaginado(0, 101).size());
        assertEquals(50, conPaginado(0, 50).size());
    }

    @Test
    void un_tamano_no_positivo_cae_al_valor_por_defecto() {
        assertEquals(20, conPaginado(0, 0).size());
        assertEquals(20, conPaginado(0, -5).size());
    }

    @Test
    void no_admite_paginas_negativas() {
        assertEquals(0, conPaginado(-3, 10).page());
    }

    @Test
    void normaliza_la_busqueda_ausente() {
        assertEquals("", new LevelSearchCriteria(null, null, null, null, 0, 10).query());
        assertEquals("blood", new LevelSearchCriteria("  blood  ", null, null, null, 0, 10).query());
    }
}
