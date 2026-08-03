package com.sagnik.ecommerce_backend.service;

import com.sagnik.ecommerce_backend.dto.CheckoutResponse;
import com.sagnik.ecommerce_backend.dto.OrderDetailResponse;
import com.sagnik.ecommerce_backend.dto.OrderSummaryResponse;

import java.util.List;

public interface OrderService {

    CheckoutResponse checkout(Long userId);

    List<OrderSummaryResponse> getOrders(Long userId);

    OrderDetailResponse getOrder(Long orderId);

    OrderDetailResponse cancelOrder(Long orderId);
}