package com.giba;

import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CodigoMensagemTest {

    @Test
    void centralizaTodosOsCodigosDoBundle() {
        String[] codigos = {
                CodigoMensagem.OPERACAO_REALIZADA,
                CodigoMensagem.REGISTRO_CADASTRADO,
                CodigoMensagem.REGISTRO_ATUALIZADO,
                CodigoMensagem.REGISTRO_EXCLUIDO,
                CodigoMensagem.CAMPO_OBRIGATORIO,
                CodigoMensagem.VALOR_INVALIDO,
                CodigoMensagem.TAMANHO_MAXIMO,
                CodigoMensagem.REGISTRO_NAO_ENCONTRADO,
                CodigoMensagem.REGISTRO_JA_EXISTE,
                CodigoMensagem.OPERACAO_NAO_REALIZADA,
                CodigoMensagem.REGISTRO_POSSUI_DEPENDENCIAS,
                CodigoMensagem.USUARIO_NAO_AUTENTICADO,
                CodigoMensagem.USUARIO_NAO_AUTORIZADO,
                CodigoMensagem.ERRO_INTERNO,
                CodigoMensagem.ERRO_ACESSO_BANCO_DADOS
        };

        assertArrayEquals(
                new String[]{
                        "MSG001", "MSG002", "MSG003", "MSG004",
                        "VAL001", "VAL002", "VAL003",
                        "NEG001", "NEG002", "NEG003", "NEG004",
                        "AUT001", "AUT002",
                        "ERR001", "ERR002"
                },
                codigos);
        assertEquals(Set.of(codigos), ResourceBundle.getBundle("messages").keySet());
    }
}
