package com.sagnik.ecommerce_backend.service;

import com.sagnik.ecommerce_backend.dto.CheckoutResponse;

public interface OrderService {

    CheckoutResponse checkout(Long userId);

}