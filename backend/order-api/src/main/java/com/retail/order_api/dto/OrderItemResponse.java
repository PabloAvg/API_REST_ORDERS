package com.retail.order_api.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,  // neto
        Integer quantity,
        BigDecimal subtotal    // neto (unitPrice * quantity)
) {}
