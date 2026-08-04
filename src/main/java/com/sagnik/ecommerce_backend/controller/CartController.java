package com.sagnik.ecommerce_backend.controller;

import com.sagnik.ecommerce_backend.dto.AddToCartRequest;
import com.sagnik.ecommerce_backend.dto.CartResponse;
import com.sagnik.ecommerce_backend.dto.UpdateCartItemRequest;
import com.sagnik.ecommerce_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public String addToCart(
            @RequestBody AddToCartRequest request) {

        cartService.addToCart(request);

        return "Product added to cart successfully.";
    }

    @GetMapping
    public CartResponse getCart() {

        return cartService.getCart();
    }

    @PutMapping("/items/{cartItemId}")
    public String updateQuantity(

            @PathVariable Long cartItemId,

            @Valid
            @RequestBody
            UpdateCartItemRequest request) {

        cartService.updateQuantity(
                cartItemId,
                request);

        return "Quantity updated successfully.";
    }

    @DeleteMapping("/items/{cartItemId}")
    public String removeItem(
            @PathVariable Long cartItemId) {

        cartService.removeItem(cartItemId);

        return "Item removed successfully.";
    }

    @DeleteMapping
    public String clearCart() {

        cartService.clearCart();

        return "Cart cleared successfully.";
    }
}