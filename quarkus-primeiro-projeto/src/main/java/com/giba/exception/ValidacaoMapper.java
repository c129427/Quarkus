package com.giba.exception;

import com.giba.dto.ErroResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

@Provider
public class ValidacaoMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String mensagem = exception.getConstraintViolations().stream()
                .map(violacao -> violacao.getPropertyPath() + ": " + violacao.getMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        return Response.status(Response.Status.BAD_REQUEST).entity(new ErroResponse(mensagem)).build();
    }
}
