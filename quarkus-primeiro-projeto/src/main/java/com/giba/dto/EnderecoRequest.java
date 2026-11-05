package com.giba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        @NotBlank @Size(max = 120) String logradouro,
        @NotBlank @Size(max = 20) String numero,
        @Size(max = 100) String complemento,
        @NotBlank @Size(max = 80) String bairro,
        @NotBlank @Size(max = 80) String cidade,
        @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String estado,
        @NotBlank @Pattern(regexp = "[0-9]{5}-?[0-9]{3}") String cep) {
}
