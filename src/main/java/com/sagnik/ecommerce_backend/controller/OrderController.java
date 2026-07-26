package com.sagnik.ecommerce_backend.controller;

import com.sagnik.ecommerce_backend.dto.CheckoutResponse;
import com.sagnik.ecommerce_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}