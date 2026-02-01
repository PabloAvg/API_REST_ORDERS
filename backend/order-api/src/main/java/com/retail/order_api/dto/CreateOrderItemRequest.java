package com.retail.order_api.dto;

import jakarta.validation.constraints.NotNull;
// @Positive impide cantidades 0 o negativas
import jakarta.validation.constraints.Positive;
// Límite máximo de cantidad por línea
import jakarta.validation.constraints.Max;

public record CreateOrderItemRequest(
    @NotNull(message = "productId es obligatorio")
    Long productId,

    @NotNull(message = "quantity es obligatorio")
    @Positive(message = "quantity debe ser mayor que 0")
    @Max(value = 10000, message = "quantity no puede ser mayor que 10000")
    Integer quantity
) {}
