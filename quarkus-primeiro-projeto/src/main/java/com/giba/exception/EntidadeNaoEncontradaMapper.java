package com.giba.exception;

import com.giba.dto.ErroResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EntidadeNaoEncontradaMapper implements ExceptionMapper<EntidadeNaoEncontradaException> {
    @Override
    public Response toResponse(EntidadeNaoEncontradaException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(new ErroResponse(exception.getMessage())).build();
    }
}
