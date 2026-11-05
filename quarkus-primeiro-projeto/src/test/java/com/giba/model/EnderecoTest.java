package com.giba.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnderecoTest {

    @Test
    void mapeiaEntidadeETamanhoDoCep() throws ReflectiveOperationException {
        assertNotNull(Endereco.class.getAnnotation(Entity.class));
        assertEquals("enderecos", Endereco.class.getAnnotation(Table.class).name());
        assertEquals(8, Endereco.class.getField("cep").getAnnotation(Column.class).length());
    }
}
