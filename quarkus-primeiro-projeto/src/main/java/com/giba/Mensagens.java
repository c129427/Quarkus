package com.giba;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Mensagens {

    private static final ResourceBundle MENSAGENS = ResourceBundle.getBundle("messages");

    private Mensagens() {
        throw new AssertionError("Esta classe não pode ser instanciada");
    }

    public static String obter(String codigo, Object... parametros) {
        String chave = codigo == null ? "null" : codigo;
        String modelo;
        try {
            modelo = MENSAGENS.getString(chave);
        } catch (MissingResourceException exception) {
            // Converte a falha do ResourceBundle em um erro claro, sem expor detalhes internos.
            throw new IllegalArgumentException("Código de mensagem não cadastrado: " + chave);
        }
        return new MessageFormat(modelo).format(parametros);
    }
}
