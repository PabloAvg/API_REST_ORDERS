package com.obramat.order_api.dto;

import com.obramat.order_api.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponse(
        Long id,
        Instant createdAt,
        OrderStatus status,
        BigDecimal totalPrice
) {}
