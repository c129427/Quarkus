package com.giba.dto;

import com.giba.model.Endereco;
import com.giba.model.Pessoa;
import com.giba.model.SituacaoPessoa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PessoaResponseTest {

    @Test
    void convertePessoaComEnderecoEOpcionalmenteSemEndereco() {
        Pessoa pessoa = new Pessoa();
        pessoa.id = 1L;
        pessoa.cpf = "52998224725";
        pessoa.nome = "Ana";
        pessoa.dataNascimento = LocalDate.of(1990, 1, 2);
        pessoa.situacao = SituacaoPessoa.ATIVA;

        PessoaResponse semEndereco = PessoaResponse.de(pessoa);
        assertEquals("Ana", semEndereco.nome());
        assertNull(semEndereco.endereco());

        Endereco endereco = new Endereco();
        endereco.id = 2L;
        endereco.cep = "01001000";
        pessoa.endereco = endereco;
        assertEquals("01001000", PessoaResponse.de(pessoa).endereco().cep());
    }
}
