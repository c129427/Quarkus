package com.giba.repository;

import com.giba.model.Pessoa;
import com.giba.model.Renda;
import com.giba.model.SituacaoPessoa;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class RendaRepositoryTest {

    @Inject
    PessoaRepository pessoas;

    @Inject
    RendaRepository rendas;

    @Test
    @TestTransaction
    void listaContaEVerificaRendasPorPeriodo() {
        Pessoa pessoa = new Pessoa();
        pessoa.cpf = "52998224725";
        pessoa.nome = "Ana";
        pessoa.dataNascimento = LocalDate.of(1990, 1, 2);
        pessoa.situacao = SituacaoPessoa.ATIVA;
        pessoas.persist(pessoa);

        Renda maisAntiga = renda(pessoa, 5, 2024);
        Renda maisRecente = renda(pessoa, 6, 2025);
        rendas.persist(List.of(maisAntiga, maisRecente));

        List<Renda> resultado = rendas.listarPorCpf(pessoa.cpf);
        assertEquals(List.of(maisRecente, maisAntiga), resultado);
        assertEquals(2, rendas.contarPorPessoa(pessoa.id));
        assertTrue(rendas.existeNoPeriodo(pessoa.id, 5, 2024, null));
        assertFalse(rendas.existeNoPeriodo(pessoa.id, 5, 2024, maisAntiga.id));
    }

    private Renda renda(Pessoa pessoa, int mes, int ano) {
        Renda renda = new Renda();
        renda.pessoa = pessoa;
        renda.mes = mes;
        renda.ano = ano;
        renda.valor = new BigDecimal("100.00");
        return renda;
    }
}
