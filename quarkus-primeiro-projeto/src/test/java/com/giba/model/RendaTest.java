package com.giba.model;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RendaTest {

    @Test
    void mapeiaUnicidadeDoPeriodoEAssociacaoObrigatoria() throws ReflectiveOperationException {
        assertTrue(java.util.Arrays.stream(Renda.class.getAnnotation(Table.class).uniqueConstraints())
                .anyMatch(constraint -> java.util.Arrays.asList(constraint.columnNames())
                        .containsAll(java.util.List.of("pessoa_id", "mes", "ano"))));
        assertFalse(Renda.class.getField("pessoa").getAnnotation(ManyToOne.class).optional());
    }
}
