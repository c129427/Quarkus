package com.giba.resource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloResourceTest {

    @Test
    void retornaMensagemDeBoasVindas() {
        assertEquals("Hello from Quarkus REST", new HelloResource().hello());
    }
}
