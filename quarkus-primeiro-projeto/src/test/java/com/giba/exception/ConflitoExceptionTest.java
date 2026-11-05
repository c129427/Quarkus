package com.giba.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConflitoExceptionTest {

    @Test
    void preservaMensagem() {
        assertEquals("Conflito", new ConflitoException("Conflito").getMessage());
    }
}
