package com.giba.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RendaRequestTest {

    @Test
    void validaPeriodoEValorNaoNegativoComDuasCasasDecimais() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            RendaRequest valido = new RendaRequest(5, 2025, new BigDecimal("12000.00"));
            RendaRequest invalido = new RendaRequest(13, 1899, new BigDecimal("-1.001"));

            assertTrue(validator.validate(valido).isEmpty());
            assertFalse(validator.validate(invalido).isEmpty());
        }
    }
}
