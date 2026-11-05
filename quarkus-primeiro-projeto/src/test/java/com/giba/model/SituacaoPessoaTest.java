package com.giba.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SituacaoPessoaTest {

    @Test
    void disponibilizaSituacoesEsperadas() {
        assertArrayEquals(new SituacaoPessoa[]{SituacaoPessoa.ATIVA, SituacaoPessoa.INATIVA},
                SituacaoPessoa.values());
    }
}
