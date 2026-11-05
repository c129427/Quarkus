package com.giba.resource;

import com.giba.dto.RendaRequest;
import com.giba.dto.RendaResponse;
import com.giba.service.RendaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/pessoas/{cpf}/rendas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RendaResource {

    @Inject
    RendaService service;

    @POST
    public Response criar(@PathParam("cpf") String cpf, @Valid RendaRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(RendaResponse.de(service.criar(cpf, request))).build();
    }

    @GET
    public List<RendaResponse> listar(@PathParam("cpf") String cpf) {
        return service.listar(cpf).stream().map(RendaResponse::de).toList();
    }

    @PUT
    @Path("/registro/{id}")
    public RendaResponse atualizar(@PathParam("cpf") String cpf, @PathParam("id") String id,
                                   @Valid RendaRequest request) {
        return RendaResponse.de(service.atualizar(cpf, IdPathParam.converter(id), request));
    }

    @DELETE
    @Path("/registro/{id}")
    public Response excluir(@PathParam("cpf") String cpf, @PathParam("id") String id) {
        service.excluir(cpf, IdPathParam.converter(id));
        return Response.noContent().build();
    }
}
