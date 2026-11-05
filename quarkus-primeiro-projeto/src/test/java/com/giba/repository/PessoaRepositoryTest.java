package com.giba.repository;

import com.giba.model.Pessoa;
import com.giba.model.SituacaoPessoa;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class PessoaRepositoryTest {

    @Inject
    PessoaRepository repository;

    @Test
    @TestTransaction
    void buscaPessoaPorCpf() {
        Pessoa pessoa = new Pessoa();
        pessoa.cpf = "52998224725";
        pessoa.nome = "Ana";
        pessoa.dataNascimento = LocalDate.of(1990, 1, 2);
        pessoa.situacao = SituacaoPessoa.ATIVA;
        repository.persist(pessoa);

        assertEquals(pessoa, repository.buscarPorCpf(pessoa.cpf).orElseThrow());
        assertTrue(repository.buscarPorCpf("11111111111").isEmpty());
    }
}
