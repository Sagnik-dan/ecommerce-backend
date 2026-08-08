package com.sagnik.ecommerce_backend.config;

import com.sagnik.ecommerce_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC ENDPOINTS
                        // =========================

                        .requestMatchers(
                                "/api/auth/**",
                                "/api/test/public",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()


                        // =========================
                        // CATEGORY - READ
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/**"
                        )
                        .hasAnyAuthority("ADMIN", "CUSTOMER")


                        // =========================
                        // CATEGORY - ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**"
                        )
                        .hasAuthority("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**"
                        )
                        .hasAuthority("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories/**"
                        )
                        .hasAuthority("ADMIN")


                        // =========================
                        // PRODUCT - READ
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**"
                        )
                        .hasAnyAuthority("ADMIN", "CUSTOMER")


                        // =========================
                        // PRODUCT - ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**"
                        )
                        .hasAuthority("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products/**"
                        )
                        .hasAuthority("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products/**"
                        )
                        .hasAuthority("ADMIN")


                        // =========================
                        // CART
                        // =========================

                        .requestMatchers("/api/cart/**")
                        .hasAnyAuthority("ADMIN", "CUSTOMER")


                        // =========================
                        // ORDERS
                        // =========================

                        .requestMatchers("/api/orders/**")
                        .hasAnyAuthority("ADMIN", "CUSTOMER")


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}