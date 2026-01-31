package com.obramat.order_api.dto;

import jakarta.validation.constraints.NotNull;
// @Positive impide cantidades 0 o negativas
import jakarta.validation.constraints.Positive;

public record CreateOrderItemRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity
) {}
