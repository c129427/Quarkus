package com.giba.resource;

import com.giba.CodigoMensagem;
import com.giba.Mensagens;
import com.giba.dto.ErroResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

final class IdPathParam {

    private IdPathParam() {
        throw new AssertionError("Esta classe não pode ser instanciada");
    }

    static Long converter(String valor) {
        try {
            return Long.valueOf(valor);
        } catch (NumberFormatException exception) {
            Response resposta = Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErroResponse(Mensagens.obter(CodigoMensagem.VALOR_INVALIDO, "id")))
                    .build();
            throw new BadRequestException(resposta);
        }
    }
}
