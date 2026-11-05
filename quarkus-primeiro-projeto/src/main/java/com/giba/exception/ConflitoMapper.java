package com.giba.exception;

import com.giba.dto.ErroResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflitoMapper implements ExceptionMapper<ConflitoException> {
    @Override
    public Response toResponse(ConflitoException exception) {
        return Response.status(Response.Status.CONFLICT).entity(new ErroResponse(exception.getMessage())).build();
    }
}
