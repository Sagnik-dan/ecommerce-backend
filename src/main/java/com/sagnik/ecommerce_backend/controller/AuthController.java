package com.sagnik.ecommerce_backend.controller;

import com.sagnik.ecommerce_backend.dto.LoginRequest;
import com.sagnik.ecommerce_backend.dto.LoginResponse;
import com.sagnik.ecommerce_backend.dto.RegisterRequest;
import com.sagnik.ecommerce_backend.dto.RegisterResponse;
import com.sagnik.ecommerce_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return userService.login(request);
    }
}