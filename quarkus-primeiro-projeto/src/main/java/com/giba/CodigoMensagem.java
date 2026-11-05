package com.giba;

public final class CodigoMensagem {

    public static final String OPERACAO_REALIZADA = "MSG001";
    public static final String REGISTRO_CADASTRADO = "MSG002";
    public static final String REGISTRO_ATUALIZADO = "MSG003";
    public static final String REGISTRO_EXCLUIDO = "MSG004";

    public static final String CAMPO_OBRIGATORIO = "VAL001";
    public static final String VALOR_INVALIDO = "VAL002";
    public static final String TAMANHO_MAXIMO = "VAL003";

    public static final String REGISTRO_NAO_ENCONTRADO = "NEG001";
    public static final String REGISTRO_JA_EXISTE = "NEG002";
    public static final String OPERACAO_NAO_REALIZADA = "NEG003";
    public static final String REGISTRO_POSSUI_DEPENDENCIAS = "NEG004";

    public static final String USUARIO_NAO_AUTENTICADO = "AUT001";
    public static final String USUARIO_NAO_AUTORIZADO = "AUT002";

    public static final String ERRO_INTERNO = "ERR001";
    public static final String ERRO_ACESSO_BANCO_DADOS = "ERR002";

    private CodigoMensagem() {
        throw new AssertionError("Esta classe não pode ser instanciada");
    }
}
