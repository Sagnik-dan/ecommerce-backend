package com.sagnik.ecommerce_backend.controller;

import com.sagnik.ecommerce_backend.dto.CheckoutResponse;
import com.sagnik.ecommerce_backend.dto.OrderDetailResponse;
import com.sagnik.ecommerce_backend.dto.OrderSummaryResponse;
import com.sagnik.ecommerce_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout/{userId}")
    public CheckoutResponse checkout(
            @PathVariable Long userId) {

        return orderService.checkout(userId);
    }

    @GetMapping("/user/{userId}")
    public List<OrderSummaryResponse> getOrders(
            @PathVariable Long userId) {

        return orderService.getOrders(userId);
    }

    @GetMapping("/{orderId}")
    public OrderDetailResponse getOrder(
            @PathVariable Long orderId) {

        return orderService.getOrder(orderId);
    }

    @PutMapping("/{orderId}/cancel")
    public OrderDetailResponse cancelOrder(
            @PathVariable Long orderId) {

        return orderService.cancelOrder(orderId);
    }
    
}