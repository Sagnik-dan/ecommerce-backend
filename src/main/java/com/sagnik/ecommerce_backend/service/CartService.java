package com.sagnik.ecommerce_backend.service;

import com.sagnik.ecommerce_backend.dto.AddToCartRequest;
import com.sagnik.ecommerce_backend.dto.CartResponse;

public interface CartService {

    void addToCart(
            Long userId,
            AddToCartRequest request);

    CartResponse getCart(Long userId);


}
