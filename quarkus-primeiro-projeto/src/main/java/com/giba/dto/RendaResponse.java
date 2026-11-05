package com.giba.dto;

import com.giba.model.Renda;

import java.math.BigDecimal;

public record RendaResponse(Long id, Integer mes, Integer ano, BigDecimal valor) {
    public static RendaResponse de(Renda renda) {
        return new RendaResponse(renda.id, renda.mes, renda.ano, renda.valor);
    }
}
