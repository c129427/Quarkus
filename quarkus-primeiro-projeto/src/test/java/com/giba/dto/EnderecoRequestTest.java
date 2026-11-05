package com.giba.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnderecoRequestTest {

    @Test
    void validaCamposObrigatoriosEstadoECep() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            EnderecoRequest valido = new EnderecoRequest(
                    "Rua A", "10", null, "Centro", "Sao Paulo", "SP", "01001-000");
            EnderecoRequest invalido = new EnderecoRequest(
                    "", "10", null, "Centro", "Sao Paulo", "S", "123");

            assertTrue(validator.validate(valido).isEmpty());
            assertFalse(validator.validate(invalido).isEmpty());
        }
    }
}
