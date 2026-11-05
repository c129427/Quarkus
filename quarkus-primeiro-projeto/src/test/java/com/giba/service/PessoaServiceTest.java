package com.giba.service;

import com.giba.dto.PessoaRequest;
import com.giba.exception.ConflitoException;
import com.giba.model.Pessoa;
import com.giba.model.SituacaoPessoa;
import com.giba.repository.PessoaRepository;
import com.giba.repository.RendaRepository;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PessoaServiceTest {

    private PessoaService service;
    private PessoaRepository pessoas;
    private RendaRepository rendas;

    @BeforeEach
    void setUp() {
        service = new PessoaService();
        pessoas = mock(PessoaRepository.class);
        rendas = mock(RendaRepository.class);
        service.pessoas = pessoas;
        service.rendas = rendas;
    }

    @Test
    void criaPessoaNormalizandoCpfENome() {
        when(pessoas.buscarPorCpf("52998224725")).thenReturn(Optional.empty());
        PessoaRequest request = new PessoaRequest("529.982.247-25", " Ana ",
                LocalDate.of(1990, 1, 2), SituacaoPessoa.ATIVA);

        Pessoa criada = service.criar(request);

        assertEquals("52998224725", criada.cpf);
        assertEquals("Ana", criada.nome);
        verify(pessoas).persist(criada);
    }

    @Test
    void rejeitaCpfInvalidoEDuplicado() {
        PessoaRequest invalida = new PessoaRequest("11111111111", "Ana",
                LocalDate.of(1990, 1, 2), SituacaoPessoa.ATIVA);
        assertThrows(BadRequestException.class, () -> service.criar(invalida));
        verify(pessoas, never()).buscarPorCpf(any());

        when(pessoas.buscarPorCpf("52998224725")).thenReturn(Optional.of(new Pessoa()));
        PessoaRequest duplicada = new PessoaRequest("52998224725", "Ana",
                LocalDate.of(1990, 1, 2), SituacaoPessoa.ATIVA);
        assertThrows(ConflitoException.class, () -> service.criar(duplicada));
        verify(pessoas, never()).persist(any(Pessoa.class));
    }
}
