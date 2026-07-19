package com.sagnik.ecommerce_backend.controller;

import com.sagnik.ecommerce_backend.dto.AddToCartRequest;
import com.sagnik.ecommerce_backend.dto.CartResponse;
import com.sagnik.ecommerce_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/add")
    public String addToCart(

            @PathVariable Long userId,

            @Valid
            @RequestBody
            AddToCartRequest request) {

        cartService.addToCart(userId, request);

        return "Product added to cart successfully.";
    }

    @GetMapping("/{userId}")
    public CartResponse getCart(
            @PathVariable Long userId) {

        return cartService.getCart(userId);
    }
}