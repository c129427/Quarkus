package com.giba.exception;

import com.giba.dto.ErroResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidacaoMapperTest {

    @Test
    void retornaViolacoesOrdenadasEmHttp400() {
        ConstraintViolation<?> nome = violacao("nome", "obrigatorio");
        ConstraintViolation<?> cpf = violacao("cpf", "invalido");
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(nome, cpf));

        try (Response response = new ValidacaoMapper().toResponse(exception)) {
            assertEquals(400, response.getStatus());
            assertEquals(new ErroResponse("cpf: invalido; nome: obrigatorio"), response.getEntity());
        }
    }

    private ConstraintViolation<?> violacao(String caminho, String mensagem) {
        Path path = mock(Path.class);
        when(path.toString()).thenReturn(caminho);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(mensagem);
        return violation;
    }
}
