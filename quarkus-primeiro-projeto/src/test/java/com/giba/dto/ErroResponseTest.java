package com.giba.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErroResponseTest {

    @Test
    void armazenaMensagemDoErro() {
        assertEquals("Falha", new ErroResponse("Falha").mensagem());
    }
}
