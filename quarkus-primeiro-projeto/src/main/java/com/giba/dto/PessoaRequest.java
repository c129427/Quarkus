package com.giba.dto;

import com.giba.model.SituacaoPessoa;
import com.giba.validation.CpfValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PessoaRequest(
        @NotBlank @CpfValido String cpf,
        @NotBlank @Size(max = 150) String nome,
        @NotNull @Past LocalDate dataNascimento,
        @NotNull SituacaoPessoa situacao) {
}
