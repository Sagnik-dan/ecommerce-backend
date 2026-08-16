package com.sagnik.ecommerce_backend.controller;

import com.sagnik.ecommerce_backend.dto.CheckoutResponse;
import com.sagnik.ecommerce_backend.dto.OrderDetailResponse;
import com.sagnik.ecommerce_backend.dto.OrderSummaryResponse;
import com.sagnik.ecommerce_backend.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public CheckoutResponse checkout() {

        return orderService.checkout();
    }

    @GetMapping("")
    public List<OrderSummaryResponse> getOrders() {

        return orderService.getOrders();
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