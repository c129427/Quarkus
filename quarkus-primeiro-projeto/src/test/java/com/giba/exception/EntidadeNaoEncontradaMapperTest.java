package com.giba.exception;

import com.giba.dto.ErroResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntidadeNaoEncontradaMapperTest {

    @Test
    void converteAusenciaEmHttp404() {
        try (Response response = new EntidadeNaoEncontradaMapper()
                .toResponse(new EntidadeNaoEncontradaException("Nao encontrada"))) {
            assertEquals(404, response.getStatus());
            assertEquals(new ErroResponse("Nao encontrada"), response.getEntity());
        }
    }
}
