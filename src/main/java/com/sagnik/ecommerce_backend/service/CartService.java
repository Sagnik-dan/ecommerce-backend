package com.sagnik.ecommerce_backend.service;

import com.sagnik.ecommerce_backend.dto.AddToCartRequest;
import com.sagnik.ecommerce_backend.dto.CartResponse;
import com.sagnik.ecommerce_backend.dto.UpdateCartItemRequest;
import com.sagnik.ecommerce_backend.security.AuthenticationService;

public interface CartService {

    void addToCart(AddToCartRequest request);

    CartResponse getCart();

    void clearCart();

    void updateQuantity(
            Long cartItemId,
            UpdateCartItemRequest request);

    void removeItem(Long cartItemId);


}
