package com.obramat.order_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequest(
    @NotEmpty(message = "Items debe contener al menos un producto")
    List<@NotNull(message = "Items no puede contener elementos nulos") @Valid CreateOrderItemRequest> items
) {}
