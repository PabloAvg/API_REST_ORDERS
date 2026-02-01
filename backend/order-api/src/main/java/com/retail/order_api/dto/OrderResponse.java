package com.retail.order_api.dto;

import com.retail.order_api.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Instant createdAt,
        OrderStatus status,
        BigDecimal totalPrice, // bruto (con IVA)
        List<OrderItemResponse> items
) {}
