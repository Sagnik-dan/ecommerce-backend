package com.sagnik.ecommerce_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long cartId;

    private BigDecimal totalPrice;

    private List<CartItemResponse> items;
}