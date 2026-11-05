package com.giba.dto;

import com.giba.model.Renda;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RendaResponseTest {

    @Test
    void converteRenda() {
        Renda renda = new Renda();
        renda.id = 3L;
        renda.mes = 5;
        renda.ano = 2025;
        renda.valor = new BigDecimal("12000.00");

        assertEquals(new RendaResponse(3L, 5, 2025, new BigDecimal("12000.00")),
                RendaResponse.de(renda));
    }
}
