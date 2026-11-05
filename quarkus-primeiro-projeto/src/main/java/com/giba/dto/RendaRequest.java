package com.giba.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record RendaRequest(
        @NotNull @Min(1) @Max(12) Integer mes,
        @NotNull @Min(1900) @Max(9999) Integer ano,
        @NotNull @PositiveOrZero @Digits(integer = 12, fraction = 2) BigDecimal valor) {
}
