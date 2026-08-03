package com.sagnik.ecommerce_backend.dto;

import com.sagnik.ecommerce_backend.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long orderId;

    private LocalDateTime orderDate;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private List<OrderItemResponse> items;
}