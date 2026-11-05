package com.giba.exception;

import com.giba.dto.ErroResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConflitoMapperTest {

    @Test
    void converteConflitoEmHttp409() {
        try (Response response = new ConflitoMapper().toResponse(new ConflitoException("Duplicado"))) {
            assertEquals(409, response.getStatus());
            assertEquals(new ErroResponse("Duplicado"), response.getEntity());
        }
    }
}
