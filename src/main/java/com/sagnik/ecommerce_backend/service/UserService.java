package com.sagnik.ecommerce_backend.service;

import com.sagnik.ecommerce_backend.dto.LoginRequest;
import com.sagnik.ecommerce_backend.dto.LoginResponse;
import com.sagnik.ecommerce_backend.dto.RegisterRequest;
import com.sagnik.ecommerce_backend.dto.RegisterResponse;

public interface UserService {

    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}