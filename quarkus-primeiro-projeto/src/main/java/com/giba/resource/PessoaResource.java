package com.giba.resource;

import com.giba.dto.EnderecoRequest;
import com.giba.dto.EnderecoResponse;
import com.giba.dto.PessoaRequest;
import com.giba.dto.PessoaResponse;
import com.giba.model.Pessoa;
import com.giba.service.PessoaService;
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

@Path("/api/pessoas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PessoaResource {

    @Inject
    PessoaService service;

    @POST
    public Response criar(@Valid PessoaRequest request) {
        Pessoa pessoa = service.criar(request);
        return Response.status(Response.Status.CREATED).entity(PessoaResponse.de(pessoa)).build();
    }

    @GET
    public List<PessoaResponse> listar() {
        return service.listar().stream().map(PessoaResponse::de).toList();
    }

    @GET
    @Path("/{id}")
    public PessoaResponse buscar(@PathParam("id") String id) {
        return PessoaResponse.de(service.buscar(IdPathParam.converter(id)));
    }

    @PUT
    @Path("/{id}")
    public PessoaResponse atualizar(@PathParam("id") String id, @Valid PessoaRequest request) {
        return PessoaResponse.de(service.atualizar(IdPathParam.converter(id), request));
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") String id) {
        service.excluir(IdPathParam.converter(id));
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/endereco")
    public EnderecoResponse atualizarEndereco(@PathParam("id") String id, @Valid EnderecoRequest request) {
        return EnderecoResponse.de(service.atualizarEndereco(IdPathParam.converter(id), request).endereco);
    }

    @DELETE
    @Path("/{id}/endereco")
    public Response excluirEndereco(@PathParam("id") String id) {
        service.excluirEndereco(IdPathParam.converter(id));
        return Response.noContent().build();
    }
}
