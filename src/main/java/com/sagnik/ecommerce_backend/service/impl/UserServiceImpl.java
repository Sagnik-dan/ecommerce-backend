package com.sagnik.ecommerce_backend.service.impl;

import com.sagnik.ecommerce_backend.dto.LoginRequest;
import com.sagnik.ecommerce_backend.dto.LoginResponse;
import com.sagnik.ecommerce_backend.dto.RegisterRequest;
import com.sagnik.ecommerce_backend.dto.RegisterResponse;
import com.sagnik.ecommerce_backend.entity.User;
import com.sagnik.ecommerce_backend.entity.UserRole;
import com.sagnik.ecommerce_backend.exception.InvalidCredentialsException;
import com.sagnik.ecommerce_backend.exception.UserAlreadyExistsException;
import com.sagnik.ecommerce_backend.repository.UserRepository;
import com.sagnik.ecommerce_backend.security.JwtService;
import com.sagnik.ecommerce_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(
                        request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid credentials"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid credentials");
        }

        String token =
                jwtService.generateToken(user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .build();
    }
}