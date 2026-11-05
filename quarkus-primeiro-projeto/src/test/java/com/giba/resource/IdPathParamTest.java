package com.giba.resource;

import com.giba.dto.ErroResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdPathParamTest {

    @Test
    void converteIdNumerico() {
        assertEquals(123L, IdPathParam.converter("123"));
    }

    @Test
    void rejeitaIdNaoNumericoComRespostaPadronizada() {
        BadRequestException exception = assertThrows(
                BadRequestException.class, () -> IdPathParam.converter(":1"));

        try (Response response = exception.getResponse()) {
            assertEquals(400, response.getStatus());
            assertEquals(new ErroResponse("O campo id possui um valor inválido."), response.getEntity());
        }
    }
}
