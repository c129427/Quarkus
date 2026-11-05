package com.giba.dto;

import com.giba.model.Endereco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnderecoResponseTest {

    @Test
    void converteEnderecoETrataNulo() {
        Endereco endereco = new Endereco();
        endereco.id = 7L;
        endereco.logradouro = "Rua A";
        endereco.numero = "10";
        endereco.bairro = "Centro";
        endereco.cidade = "Sao Paulo";
        endereco.estado = "SP";
        endereco.cep = "01001000";

        assertEquals(new EnderecoResponse(7L, "Rua A", "10", null,
                "Centro", "Sao Paulo", "SP", "01001000"), EnderecoResponse.de(endereco));
        assertNull(EnderecoResponse.de(null));
    }
}
