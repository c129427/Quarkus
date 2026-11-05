package com.giba.service;

import com.giba.dto.RendaRequest;
import com.giba.exception.ConflitoException;
import com.giba.model.Pessoa;
import com.giba.model.Renda;
import com.giba.repository.PessoaRepository;
import com.giba.repository.RendaRepository;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RendaServiceTest {

    private RendaService service;
    private PessoaRepository pessoas;
    private RendaRepository rendas;

    @BeforeEach
    void setUp() {
        service = new RendaService();
        pessoas = mock(PessoaRepository.class);
        rendas = mock(RendaRepository.class);
        service.pessoas = pessoas;
        service.rendas = rendas;
    }

    @Test
    void criaRendaAssociadaAPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.id = 4L;
        pessoa.cpf = "52998224725";
        when(pessoas.buscarPorCpf(pessoa.cpf)).thenReturn(Optional.of(pessoa));
        when(rendas.existeNoPeriodo(pessoa.id, 5, 2025, null)).thenReturn(false);
        RendaRequest request = new RendaRequest(5, 2025, new BigDecimal("12000.00"));

        Renda criada = service.criar(pessoa.cpf, request);

        assertEquals(pessoa, criada.pessoa);
        assertEquals(request.valor(), criada.valor);
        verify(rendas).persist(criada);
    }

    @Test
    void rejeitaCpfInvalidoEPeriodoDuplicado() {
        assertThrows(BadRequestException.class,
                () -> service.listar("nao-e-cpf"));

        Pessoa pessoa = new Pessoa();
        pessoa.id = 4L;
        pessoa.cpf = "52998224725";
        when(pessoas.buscarPorCpf(pessoa.cpf)).thenReturn(Optional.of(pessoa));
        when(rendas.existeNoPeriodo(pessoa.id, 5, 2025, null)).thenReturn(true);
        RendaRequest request = new RendaRequest(5, 2025, new BigDecimal("12000.00"));

        assertThrows(ConflitoException.class, () -> service.criar(pessoa.cpf, request));
    }
}
