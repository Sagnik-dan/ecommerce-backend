package com.sagnik.ecommerce_backend.service;

import com.sagnik.ecommerce_backend.dto.AddToCartRequest;
import com.sagnik.ecommerce_backend.dto.CartResponse;
import com.sagnik.ecommerce_backend.dto.UpdateCartItemRequest;

public interface CartService {

    void addToCart(
            Long userId,
            AddToCartRequest request);

    CartResponse getCart(Long userId);

    void updateQuantity(
            Long cartItemId,
            UpdateCartItemRequest request);

    void removeItem(Long cartItemId);

    void clearCart(Long userId);

}
