package com.giba.dto;

import com.giba.model.SituacaoPessoa;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PessoaRequestTest {

    @Test
    void validaCpfNomeNascimentoESituacao() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            PessoaRequest valido = new PessoaRequest(
                    "529.982.247-25", "Ana Souza", LocalDate.of(1990, 1, 2), SituacaoPessoa.ATIVA);
            PessoaRequest invalido = new PessoaRequest(
                    "111.111.111-11", "", LocalDate.now().plusDays(1), null);

            assertTrue(validator.validate(valido).isEmpty());
            assertFalse(validator.validate(invalido).isEmpty());
        }
    }
}
