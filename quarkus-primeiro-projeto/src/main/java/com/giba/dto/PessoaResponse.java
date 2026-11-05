package com.giba.dto;

import com.giba.model.Pessoa;
import com.giba.model.SituacaoPessoa;

import java.time.LocalDate;

public record PessoaResponse(Long id, String cpf, String nome, LocalDate dataNascimento,
                             SituacaoPessoa situacao, EnderecoResponse endereco) {
    public static PessoaResponse de(Pessoa pessoa) {
        return new PessoaResponse(pessoa.id, pessoa.cpf, pessoa.nome, pessoa.dataNascimento,
                pessoa.situacao, EnderecoResponse.de(pessoa.endereco));
    }
}
