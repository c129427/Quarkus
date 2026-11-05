package com.giba;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MensagensTest {

    @Test
    void obtemMensagemSemParametros() {
        assertEquals("Operação realizada com sucesso.",
                Mensagens.obter(CodigoMensagem.OPERACAO_REALIZADA));
    }

    @Test
    void obtemMensagemComUmParametro() {
        assertEquals("O campo nome é obrigatório.",
                Mensagens.obter(CodigoMensagem.CAMPO_OBRIGATORIO, "nome"));
    }

    @Test
    void obtemMensagemComDoisParametros() {
        assertEquals("O campo nome deve possuir no máximo 100 caracteres.",
                Mensagens.obter(CodigoMensagem.TAMANHO_MAXIMO, "nome", 100));
    }

    @Test
    void rejeitaCodigoDeMensagemInexistenteComErroClaro() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Mensagens.obter("CODIGO_INEXISTENTE"));

        assertTrue(exception.getMessage().contains("CODIGO_INEXISTENTE"));
        assertTrue(exception.getMessage().contains("não cadastrado"));
        assertNull(exception.getCause());
    }
}
