package com.sagnik.ecommerce_backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private String productName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;
}