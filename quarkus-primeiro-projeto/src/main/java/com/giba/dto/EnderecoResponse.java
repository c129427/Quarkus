package com.giba.dto;

import com.giba.model.Endereco;

public record EnderecoResponse(Long id, String logradouro, String numero, String complemento,
                               String bairro, String cidade, String estado, String cep) {
    public static EnderecoResponse de(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoResponse(endereco.id, endereco.logradouro, endereco.numero,
                endereco.complemento, endereco.bairro, endereco.cidade, endereco.estado, endereco.cep);
    }
}
