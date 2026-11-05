package com.giba.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfValidatorTest {

    @Test
    void validaNormalizaERejeitaCpfsInvalidos() {
        CpfValidator validator = new CpfValidator();
        assertTrue(CpfValidator.validar("529.982.247-25"));
        assertFalse(CpfValidator.validar("111.111.111-11"));
        assertFalse(CpfValidator.validar("cpf"));
        assertTrue(validator.isValid(null, null));
        assertEquals("52998224725", CpfValidator.normalizar("529.982.247-25"));
        assertThrows(IllegalArgumentException.class, () -> CpfValidator.normalizar("123"));
    }
}
