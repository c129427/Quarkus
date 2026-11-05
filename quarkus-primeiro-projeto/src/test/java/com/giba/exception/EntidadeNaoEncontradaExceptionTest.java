package com.giba.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntidadeNaoEncontradaExceptionTest {

    @Test
    void preservaMensagem() {
        assertEquals("Nao encontrada",
                new EntidadeNaoEncontradaException("Nao encontrada").getMessage());
    }
}
