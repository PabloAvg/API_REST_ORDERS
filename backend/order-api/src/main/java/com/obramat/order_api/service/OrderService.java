package com.obramat.order_api.service;

import com.obramat.order_api.dto.*;
import com.obramat.order_api.entity.*;
import com.obramat.order_api.exception.BadRequestException;
import com.obramat.order_api.exception.NotFoundException;
import com.obramat.order_api.repository.OrderRepository;
import com.obramat.order_api.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private static final BigDecimal VAT_MULTIPLIER = new BigDecimal("1.21");

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        // Validación extra: evitar productId duplicados en el mismo pedido
        Set<Long> seen = new HashSet<>();
        for (CreateOrderItemRequest item : request.items()) {
            if (!seen.add(item.productId())) {
                throw new BadRequestException("Producto duplicado en el pedido: productId=" + item.productId());
            }
        }

        Order order = new Order();
        order.setCreatedAt(Instant.now());
        order.setStatus(OrderStatus.PENDING); // requisito

        BigDecimal netTotal = BigDecimal.ZERO;

        for (CreateOrderItemRequest item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado: id=" + item.productId()));

            // snapshot del precio neto actual
            BigDecimal unitPriceNet = product.getPrice();

            OrderDetail detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(item.quantity());
            detail.setUnitPrice(unitPriceNet);
            order.addDetail(detail);

            BigDecimal lineNet = unitPriceNet.multiply(BigDecimal.valueOf(item.quantity()));
            netTotal = netTotal.add(lineNet);
        }

        // total bruto (con IVA)
        BigDecimal grossTotal = netTotal.multiply(VAT_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
        order.setTotalPrice(grossTotal);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> list() {
        return orderRepository.findAll().stream()
                .map(o -> new OrderSummaryResponse(o.getId(), o.getCreatedAt(), o.getStatus(), o.getTotalPrice()))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado: id=" + id));
        return toResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado: id=" + id));
        orderRepository.delete(order);
    }

    private OrderResponse toResponse(Order order) {
        var items = order.getDetails().stream().map(d -> {
            BigDecimal subtotalNet = d.getUnitPrice()
                    .multiply(BigDecimal.valueOf(d.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            return new OrderItemResponse(
                    d.getProduct().getId(),
                    d.getProduct().getName(),
                    d.getUnitPrice(),
                    d.getQuantity(),
                    subtotalNet
            );
        }).toList();

        return new OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalPrice(),
                items
        );
    }
}
