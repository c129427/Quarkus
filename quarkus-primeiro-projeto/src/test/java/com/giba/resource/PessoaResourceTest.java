package com.giba.resource;

import com.giba.dto.PessoaRequest;
import com.giba.model.Pessoa;
import com.giba.model.SituacaoPessoa;
import com.giba.service.PessoaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PessoaResourceTest {

    private PessoaResource resource;
    private PessoaService service;

    @BeforeEach
    void setUp() {
        resource = new PessoaResource();
        service = mock(PessoaService.class);
        resource.service = service;
    }

    @Test
    void criaPessoaComRespostaHttp201() {
        PessoaRequest request = new PessoaRequest(
                "52998224725", "Ana", LocalDate.of(1990, 1, 2), SituacaoPessoa.ATIVA);
        Pessoa pessoa = new Pessoa();
        pessoa.id = 1L;
        pessoa.cpf = request.cpf();
        pessoa.nome = request.nome();
        pessoa.dataNascimento = request.dataNascimento();
        pessoa.situacao = request.situacao();
        when(service.criar(request)).thenReturn(pessoa);

        try (Response response = resource.criar(request)) {
            assertEquals(201, response.getStatus());
            assertEquals("Ana", assertInstanceOf(com.giba.dto.PessoaResponse.class,
                    response.getEntity()).nome());
        }
        verify(service).criar(request);
    }
}
